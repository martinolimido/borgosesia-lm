package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Un contratto può essere CESSATO con cessazione programmata in futuro
 * (rientra nello scope di verifica). Segnaliamo come info-warning la
 * combinazione perché spesso indica un workflow di programmazione cessazione
 * che richiede follow-up.
 */
@Component
public class DataCessazioneFuturaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.DATA_CESSAZIONE_FUTURA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() == ContrattoStato.CESSATO
                && contratto.getDataCessazione() != null
                && contratto.getDataCessazione().isAfter(ctx.getOggi())) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.DATA_CESSAZIONE_FUTURA,
                    "Contratto marcato CESSATO ma dataCessazione (" + contratto.getDataCessazione()
                            + ") è futura rispetto a oggi (" + ctx.getOggi() + ")"));
        }
        return RisultatoRegola.vuoto();
    }
}
