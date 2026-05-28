package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.TipologiaRinnovo;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Un contratto puramente tacito non dovrebbe avere dataFine: concettualmente
 * va avanti per sempre. Se è valorizzata, segnaliamo l'incoerenza.
 */
@Component
public class RinnovoTacitoConDataFineRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.RINNOVO_TACITO_CON_DATA_FINE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getTipologiaRinnovo() == TipologiaRinnovo.tacito
                && contratto.getDataFine() != null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.RINNOVO_TACITO_CON_DATA_FINE,
                    "Contratto con tipologia di rinnovo TACITO ma dataFine valorizzata ("
                            + contratto.getDataFine() + "): per definizione non dovrebbe avere scadenza"));
        }
        return RisultatoRegola.vuoto();
    }
}
