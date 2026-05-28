package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class DataFinePrecedenteInizioRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.DATA_FINE_PRECEDENTE_INIZIO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getDataInizio() != null
                && contratto.getDataFine() != null
                && contratto.getDataFine().isBefore(contratto.getDataInizio())) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.DATA_FINE_PRECEDENTE_INIZIO,
                    "dataFine (" + contratto.getDataFine() + ") precedente a dataInizio ("
                            + contratto.getDataInizio() + ")"));
        }
        return RisultatoRegola.vuoto();
    }
}
