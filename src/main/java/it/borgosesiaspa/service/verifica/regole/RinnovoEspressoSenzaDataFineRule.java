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

@Component
public class RinnovoEspressoSenzaDataFineRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.RINNOVO_ESPRESSO_SENZA_DATA_FINE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getTipologiaRinnovo() == TipologiaRinnovo.espresso
                && contratto.getDataFine() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.RINNOVO_ESPRESSO_SENZA_DATA_FINE,
                    "Contratto con tipologia di rinnovo ESPRESSO senza dataFine valorizzata"));
        }
        return RisultatoRegola.vuoto();
    }
}
