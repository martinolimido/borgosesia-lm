package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * NB: scelta progettuale: il ricalcolo automatico della
 * {@code dataProssimaRivalutazioneIstat} NON è autorizzato come azione
 * automatica. Questa regola si limita a segnalare l'anomalia.
 */
@Component
public class IstatRivalutazioneScadutaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.ISTAT_RIVALUTAZIONE_SCADUTA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (!Boolean.TRUE.equals(contratto.getRivalutazioneIstat())) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getDataProssimaRivalutazioneIstat() == null) {
            return RisultatoRegola.vuoto(); // gestito da IstatAttivaSenzaDataProssimaRule
        }
        if (contratto.getDataProssimaRivalutazioneIstat().isBefore(ctx.getOggi())) {
            String payload = "{\"dataProssimaRivalutazioneIstat\":\""
                    + contratto.getDataProssimaRivalutazioneIstat() + "\",\"oggi\":\""
                    + ctx.getOggi() + "\"}";
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.ISTAT_RIVALUTAZIONE_SCADUTA,
                    "Rivalutazione ISTAT attesa al " + contratto.getDataProssimaRivalutazioneIstat()
                            + " non ancora effettuata",
                    payload));
        }
        return RisultatoRegola.vuoto();
    }
}
