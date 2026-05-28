package it.borgosesiaspa.service.verifica.regole;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Segnala (INFO) quando ci si trova all'interno della finestra di preavviso
 * per la dataFine o la dataPrimaScadenza: utile come reminder operativo.
 */
@Component
public class DisdettaInFinestraPreavvisoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.DISDETTA_IN_FINESTRA_PREAVVISO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        Integer preavviso = contratto.getMesiPreavviso();
        if (preavviso == null || preavviso <= 0) {
            return RisultatoRegola.vuoto();
        }
        LocalDate scadenzaUtile = scadenzaUtile(contratto);
        if (scadenzaUtile == null) {
            return RisultatoRegola.vuoto();
        }
        LocalDate finestraInizio = scadenzaUtile.minusMonths(preavviso);
        if (!ctx.getOggi().isBefore(finestraInizio) && ctx.getOggi().isBefore(scadenzaUtile)) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.DISDETTA_IN_FINESTRA_PREAVVISO,
                    "Finestra di preavviso aperta: scadenza " + scadenzaUtile
                            + ", preavviso di " + preavviso + " mesi (da " + finestraInizio + ")"));
        }
        return RisultatoRegola.vuoto();
    }

    private LocalDate scadenzaUtile(ContrattoLocazione c) {
        if (c.getDataPrimaScadenza() != null) {
            return c.getDataPrimaScadenza();
        }
        return c.getDataFine();
    }
}
