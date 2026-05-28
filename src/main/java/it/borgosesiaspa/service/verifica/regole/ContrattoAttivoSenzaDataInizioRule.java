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
public class ContrattoAttivoSenzaDataInizioRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CONTRATTO_ATTIVO_SENZA_DATA_INIZIO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() == ContrattoStato.ATTIVO && contratto.getDataInizio() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CONTRATTO_ATTIVO_SENZA_DATA_INIZIO,
                    "Contratto in stato ATTIVO senza dataInizio valorizzata"));
        }
        return RisultatoRegola.vuoto();
    }
}
