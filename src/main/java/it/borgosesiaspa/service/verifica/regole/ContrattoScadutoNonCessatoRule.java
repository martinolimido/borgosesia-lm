package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.TipologiaRinnovo;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Contratti in stato ATTIVO con rinnovo ESPRESSO la cui dataFine è già passata
 * e non sono stati cessati: dovrebbero essere chiusi o rinnovati manualmente.
 */
@Component
public class ContrattoScadutoNonCessatoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CONTRATTO_SCADUTO_NON_CESSATO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getTipologiaRinnovo() != TipologiaRinnovo.espresso) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getDataFine() == null) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getDataFine().isBefore(ctx.getOggi()) && contratto.getDataCessazione() == null) {
            String payload = "{\"dataFine\":\"" + contratto.getDataFine() + "\",\"oggi\":\"" + ctx.getOggi() + "\"}";
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CONTRATTO_SCADUTO_NON_CESSATO,
                    "Contratto ATTIVO con rinnovo ESPRESSO e dataFine " + contratto.getDataFine()
                            + " scaduta, ma non risulta cessato",
                    payload));
        }
        return RisultatoRegola.vuoto();
    }
}
