package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.TipologiaRinnovo;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class PrimaScadenzaMancanteRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.PRIMA_SCADENZA_MANCANTE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getTipologiaRinnovo() == TipologiaRinnovo.tacito_al_primo_rinnovo
                && contratto.getDataPrimaScadenza() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.PRIMA_SCADENZA_MANCANTE,
                    "Contratto tacito_al_primo_rinnovo senza dataPrimaScadenza valorizzata"));
        }
        return RisultatoRegola.vuoto();
    }
}
