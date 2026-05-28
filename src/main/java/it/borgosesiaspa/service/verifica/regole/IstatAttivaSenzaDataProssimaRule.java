package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class IstatAttivaSenzaDataProssimaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.ISTAT_ATTIVA_SENZA_DATA_PROSSIMA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (Boolean.TRUE.equals(contratto.getRivalutazioneIstat())
                && contratto.getDataProssimaRivalutazioneIstat() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.ISTAT_ATTIVA_SENZA_DATA_PROSSIMA,
                    "Rivalutazione ISTAT abilitata ma dataProssimaRivalutazioneIstat non valorizzata"));
        }
        return RisultatoRegola.vuoto();
    }
}
