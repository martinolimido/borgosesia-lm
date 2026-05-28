package it.borgosesiaspa.service.verifica;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.borgosesiaspa.repository.ContrattoLocazioneRepository;

/**
 * Scheduler che esegue quotidianamente il task di verifica contratti.
 *
 * <p>L'orario è configurabile tramite {@code borgosesia.verifica-contratti.cron}
 * (default 02:00). Il task può essere disabilitato globalmente impostando
 * {@code borgosesia.verifica-contratti.enabled=false}.</p>
 *
 * <p>Strategia transazionale: lo scheduler carica solo gli ID dei contratti in
 * scope; poi per ogni id chiama {@link AnomaliaContrattoReconciler#processaContratto}
 * che apre la propria transazione, carica il contratto e lo elabora end-to-end.
 * In questo modo un errore su un singolo contratto non rolla back gli altri,
 * e le relazioni lazy del contratto sono caricabili dentro la transazione.</p>
 */
@Component
public class ContrattoLocazioneVerificaScheduler {

    private static final Logger log = LoggerFactory.getLogger(ContrattoLocazioneVerificaScheduler.class);

    private final ContrattoLocazioneRepository contrattoRepository;
    private final AnomaliaContrattoReconciler reconciler;
    private final VerificaContrattiProperties properties;

    public ContrattoLocazioneVerificaScheduler(
            ContrattoLocazioneRepository contrattoRepository,
            AnomaliaContrattoReconciler reconciler,
            VerificaContrattiProperties properties) {
        this.contrattoRepository = contrattoRepository;
        this.reconciler = reconciler;
        this.properties = properties;
    }

    @Scheduled(cron = "${borgosesia.verifica-contratti.cron:0 0 2 * * *}")
    public void runSchedulato() {
        if (!properties.isEnabled()) {
            log.info("Task verifica contratti disabilitato (borgosesia.verifica-contratti.enabled=false). Skip.");
            return;
        }
        try {
            RisultatoRun risultato = esegui();
            log.info("Verifica contratti completata: {}", riassunto(risultato));
        } catch (RuntimeException e) {
            log.error("Verifica contratti FALLITA: {}", e.getMessage(), e);
        }
    }

    /**
     * Esegue un run completo del task. Pubblico per consentire l'invocazione
     * manuale via REST.
     */
    public RisultatoRun esegui() {
        LocalDateTime inizio = LocalDateTime.now();
        LocalDate oggi = LocalDate.now();
        ContestoVerifica ctx = new ContestoVerifica(oggi, properties.isAzioniAutomaticheAbilitate());

        List<Long> ids = contrattoRepository.findIdContrattiInScopeVerifica(oggi);
        log.info("Verifica contratti — avvio run su {} contratti (azioni-automatiche={})",
                ids.size(), ctx.isAzioniAutomaticheAbilitate());

        int aperte = 0;
        int aggiornate = 0;
        int risolte = 0;
        int azioni = 0;
        int errori = 0;

        for (Long id : ids) {
            try {
                AnomaliaContrattoReconciler.StatistichePerContratto stats =
                        reconciler.processaContratto(id, ctx);
                aperte += stats.aperte;
                aggiornate += stats.aggiornate;
                risolte += stats.risolte;
                azioni += stats.azioniEseguite;
            } catch (RuntimeException e) {
                errori++;
                log.error("Errore in verifica contratto id={}: {}", id, e.getMessage(), e);
            }
        }

        LocalDateTime fine = LocalDateTime.now();
        return new RisultatoRun(inizio, fine, ids.size(), aperte, aggiornate, risolte, azioni, errori);
    }

    private String riassunto(RisultatoRun r) {
        return "contratti=" + r.getContrattiAnalizzati()
                + " aperte=" + r.getAnomalieAperte()
                + " aggiornate=" + r.getAnomalieAggiornate()
                + " risolte=" + r.getAnomalieRisolte()
                + " azioni=" + r.getAzioniEseguite()
                + " errori=" + r.getErroriContratto()
                + " durata=" + java.time.Duration.between(r.getInizio(), r.getFine()).toMillis() + "ms";
    }
}
