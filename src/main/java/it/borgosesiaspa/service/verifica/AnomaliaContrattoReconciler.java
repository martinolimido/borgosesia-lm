package it.borgosesiaspa.service.verifica;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.borgosesiaspa.dto.edit.EventoContrattoEditDto;
import it.borgosesiaspa.model.AnomaliaContratto;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.EventoRiferimento;
import it.borgosesiaspa.model.enums.EventoTipo;
import it.borgosesiaspa.model.enums.StatoAnomalia;
import it.borgosesiaspa.repository.AnomaliaContrattoRepository;
import it.borgosesiaspa.repository.ContrattoLocazioneRepository;
import it.borgosesiaspa.service.EventoContrattoService;

/**
 * Orchestratore per la verifica di un singolo contratto. Carica il contratto e
 * tutte le sue relazioni nella stessa transazione in cui vengono applicate le
 * regole, eseguite le azioni automatiche e riconciliate le anomalie persistite.
 *
 * <p>Idempotente: invocazioni successive con lo stesso input non duplicano
 * record di {@link AnomaliaContratto} né eventi. Il "matching" tra anomalie già
 * persistite e anomalie rilevate avviene tramite
 * {@code hashFingerprint = SHA-256(contrattoId|codiceAnomalia|chiaveExtra)}.</p>
 */
@Service
public class AnomaliaContrattoReconciler {

    private static final Logger log = LoggerFactory.getLogger(AnomaliaContrattoReconciler.class);
    private static final String SCHEDULER_USER = "scheduler-verifica";

    private final AnomaliaContrattoRepository anomaliaRepository;
    private final ContrattoLocazioneRepository contrattoRepository;
    private final ContrattoValidatorService validator;
    private final EventoContrattoService eventoContrattoService;

    public AnomaliaContrattoReconciler(
            AnomaliaContrattoRepository anomaliaRepository,
            ContrattoLocazioneRepository contrattoRepository,
            ContrattoValidatorService validator,
            EventoContrattoService eventoContrattoService) {
        this.anomaliaRepository = anomaliaRepository;
        this.contrattoRepository = contrattoRepository;
        this.validator = validator;
        this.eventoContrattoService = eventoContrattoService;
    }

    /**
     * Carica il contratto, applica tutte le regole, esegue le eventuali azioni
     * automatiche e riconcilia le anomalie persistite. Tutto in una singola
     * transazione, così le relazioni lazy del contratto sono accessibili senza
     * LazyInitializationException.
     */
    @Transactional
    public StatistichePerContratto processaContratto(Long contrattoId, ContestoVerifica ctx) {
        ContrattoLocazione contratto = contrattoRepository.findById(contrattoId).orElse(null);
        if (contratto == null) {
            log.warn("Contratto id={} non trovato durante il run di verifica", contrattoId);
            return new StatistichePerContratto();
        }
        RisultatoValidazione risultato = validator.valida(contratto, ctx);
        return riconcilia(contratto, risultato, ctx);
    }

    /**
     * Variante "puro orchestrator" — il contratto è già caricato e managed
     * dalla transazione corrente. Utile dai test e da chiamanti che gestiscono
     * direttamente il caricamento.
     */
    @Transactional
    public StatistichePerContratto riconcilia(
            ContrattoLocazione contratto,
            RisultatoValidazione risultato,
            ContestoVerifica ctx) {

        StatistichePerContratto stats = new StatistichePerContratto();
        LocalDateTime ora = LocalDateTime.now();

        // 1) Esegui le azioni automatiche (se abilitate): possono cambiare lo
        //    stato del contratto e quindi il fingerprint delle anomalie.
        if (ctx.isAzioniAutomaticheAbilitate()) {
            for (AzioneAutomatica azione : risultato.getAzioni()) {
                try {
                    String snapshot = azione.applica(contratto);
                    contrattoRepository.save(contratto);
                    registraEventoAzione(contratto, azione, snapshot, ctx.getOggi());
                    stats.azioniEseguite++;
                    log.info("Azione automatica {} eseguita su contratto id={}",
                            azione.codice(), contratto.getId());
                } catch (RuntimeException e) {
                    log.error("Azione automatica {} fallita su contratto id={}: {}",
                            azione.codice(), contratto.getId(), e.getMessage(), e);
                }
            }
        } else if (!risultato.getAzioni().isEmpty()) {
            log.info("Azioni automatiche disabilitate: {} azioni saltate sul contratto id={}",
                    risultato.getAzioni().size(), contratto.getId());
        }

        // 2) Calcola fingerprint per ogni anomalia rilevata
        Map<String, AnomaliaRilevata> rilevatePerFingerprint = new HashMap<>();
        for (AnomaliaRilevata a : risultato.getAnomalie()) {
            String fp = fingerprint(contratto.getId(), a);
            rilevatePerFingerprint.putIfAbsent(fp, a);
        }

        // 3) Carica le anomalie attualmente APERTE sul contratto
        List<AnomaliaContratto> aperteEsistenti = anomaliaRepository
                .findByContrattoLocazioneIdAndStato(contratto.getId(), StatoAnomalia.APERTA);
        Set<String> fingerprintsCorrenti = new HashSet<>();

        // 4) Apri/aggiorna le anomalie rilevate
        for (Map.Entry<String, AnomaliaRilevata> e : rilevatePerFingerprint.entrySet()) {
            String fp = e.getKey();
            AnomaliaRilevata rilevata = e.getValue();
            fingerprintsCorrenti.add(fp);
            AnomaliaContratto persistita = anomaliaRepository
                    .findFirstByHashFingerprintAndStato(fp, StatoAnomalia.APERTA)
                    .orElse(null);
            if (persistita == null) {
                AnomaliaContratto nuova = new AnomaliaContratto();
                nuova.setContrattoLocazione(contratto);
                nuova.setCodice(rilevata.getCodice());
                nuova.setSeverita(rilevata.getSeverita());
                nuova.setStato(StatoAnomalia.APERTA);
                nuova.setHashFingerprint(fp);
                nuova.setDataRilevazione(ora);
                nuova.setDataUltimoRiscontro(ora);
                nuova.setDescrizione(rilevata.getDescrizione());
                nuova.setPayloadJson(rilevata.getPayloadJson());
                anomaliaRepository.save(nuova);
                stats.aperte++;
            } else {
                persistita.setDataUltimoRiscontro(ora);
                persistita.setDescrizione(rilevata.getDescrizione());
                persistita.setPayloadJson(rilevata.getPayloadJson());
                persistita.setSeverita(rilevata.getSeverita());
                anomaliaRepository.save(persistita);
                stats.aggiornate++;
            }
        }

        // 5) Chiudi le anomalie APERTE non più rilevate
        for (AnomaliaContratto esistente : aperteEsistenti) {
            if (!fingerprintsCorrenti.contains(esistente.getHashFingerprint())) {
                esistente.setStato(StatoAnomalia.RISOLTA);
                esistente.setDataRisoluzione(ora);
                anomaliaRepository.save(esistente);
                stats.risolte++;
            }
        }

        return stats;
    }

    private void registraEventoAzione(
            ContrattoLocazione contratto, AzioneAutomatica azione, String snapshot, LocalDate dataEvento) {
        EventoContrattoEditDto dto = new EventoContrattoEditDto();
        dto.setContrattoLocazioneId(contratto.getId());
        dto.setTipoEvento(EventoTipo.VARIAZIONE_CANONE);
        dto.setDataEvento(dataEvento);
        dto.setRiferimentoTipo(EventoRiferimento.CONTRATTO);
        dto.setRiferimentoId(contratto.getId());
        dto.setPayloadJson(snapshot != null ? snapshot : "{}");
        dto.setNote("[" + azione.codice() + "] " + azione.descrizione());
        dto.setCreatedBy(SCHEDULER_USER);
        eventoContrattoService.createEventoContratto(dto);
    }

    /**
     * Hash SHA-256 (esadecimale, primi 32 char) di
     * {@code contrattoId|codiceAnomalia|chiaveExtra}. Stabile e deterministico.
     */
    private String fingerprint(Long contrattoId, AnomaliaRilevata a) {
        String raw = String.valueOf(contrattoId) + "|" + a.getCodice().name() + "|"
                + (a.getChiaveExtra() != null ? a.getChiaveExtra() : "");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16 && i < digest.length; i++) {
                sb.append(String.format("%02x", digest[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(raw.hashCode());
        }
    }

    public static final class StatistichePerContratto {
        public int aperte;
        public int aggiornate;
        public int risolte;
        public int azioniEseguite;

        public List<String> riassunto() {
            List<String> parti = new ArrayList<>(4);
            if (aperte > 0) parti.add("aperte=" + aperte);
            if (aggiornate > 0) parti.add("aggiornate=" + aggiornate);
            if (risolte > 0) parti.add("risolte=" + risolte);
            if (azioniEseguite > 0) parti.add("azioni=" + azioniEseguite);
            return parti;
        }
    }
}
