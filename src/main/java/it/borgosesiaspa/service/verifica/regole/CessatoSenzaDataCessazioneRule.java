package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class CessatoSenzaDataCessazioneRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CESSATO_SENZA_DATA_CESSAZIONE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() == ContrattoStato.CESSATO && contratto.getDataCessazione() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CESSATO_SENZA_DATA_CESSAZIONE,
                    "Contratto CESSATO senza dataCessazione valorizzata"));
        }
        return RisultatoRegola.vuoto();
    }
}
