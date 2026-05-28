package it.borgosesiaspa.service.verifica.regole;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.Periodicita;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Per ogni piano canone attivo (eccetto UNA_TANTUM) verifica che esista almeno
 * un Canone con periodo che copre la data odierna. Se non esiste e il piano è
 * attivo da abbastanza tempo da generare almeno una rata, segnala anomalia.
 */
@Component
public class CanoneAttesoNonGeneratoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CANONE_ATTESO_NON_GENERATO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        List<PianoCanone> pianiAttivi = PianoCanoneUtil.pianiVivi(contratto).stream()
                .filter(p -> PianoCanoneUtil.attivoAllaData(p, ctx.getOggi()))
                .filter(p -> p.getPeriodicita() != null && p.getPeriodicita() != Periodicita.UNA_TANTUM)
                .toList();
        if (pianiAttivi.isEmpty()) {
            return RisultatoRegola.vuoto();
        }
        List<Canone> canoni = contratto.getCanoni() != null ? contratto.getCanoni() : List.of();
        RisultatoRegola.Builder builder = RisultatoRegola.builder();
        for (PianoCanone p : pianiAttivi) {
            LocalDate sogliaGenerazione = sogliaPrimaRata(p);
            if (sogliaGenerazione == null || ctx.getOggi().isBefore(sogliaGenerazione)) {
                continue; // piano troppo recente: nessuna rata ancora attesa
            }
            boolean trovato = canoni.stream()
                    .filter(c -> c.getPianoCanone() != null
                            && p.getId() != null
                            && p.getId().equals(c.getPianoCanone().getId()))
                    .anyMatch(c -> copreOggi(c, ctx.getOggi()));
            if (!trovato) {
                String chiave = "piano:" + p.getId();
                String payload = "{\"pianoCanoneId\":" + p.getId() + ",\"periodicita\":\""
                        + p.getPeriodicita() + "\",\"sogliaPrimaRata\":\"" + sogliaGenerazione + "\"}";
                builder.aggiungiAnomalia(AnomaliaRilevata.di(
                        CodiceAnomalia.CANONE_ATTESO_NON_GENERATO,
                        chiave,
                        "Piano canone id=" + p.getId() + " (" + p.getPeriodicita()
                                + ") attivo: atteso un Canone che copra il " + ctx.getOggi()
                                + " ma non esiste",
                        payload));
            }
        }
        return builder.build();
    }

    private boolean copreOggi(Canone c, LocalDate oggi) {
        if (c.getPeriodoDa() == null) {
            return false;
        }
        if (oggi.isBefore(c.getPeriodoDa())) {
            return false;
        }
        if (c.getPeriodoA() != null && oggi.isAfter(c.getPeriodoA())) {
            return false;
        }
        return true;
    }

    private LocalDate sogliaPrimaRata(PianoCanone p) {
        if (p.getDataInizioValidita() == null || p.getPeriodicita() == null) {
            return null;
        }
        switch (p.getPeriodicita()) {
            case MENSILE:
                return p.getDataInizioValidita().plusMonths(1);
            case BIMESTRALE:
                return p.getDataInizioValidita().plusMonths(2);
            case TRIMESTRALE:
                return p.getDataInizioValidita().plusMonths(3);
            case QUADRIMESTRALE:
                return p.getDataInizioValidita().plusMonths(4);
            case SEMESTRALE:
                return p.getDataInizioValidita().plusMonths(6);
            case ANNUALE:
                return p.getDataInizioValidita().plusMonths(12);
            case UNA_TANTUM:
            default:
                return null;
        }
    }
}
