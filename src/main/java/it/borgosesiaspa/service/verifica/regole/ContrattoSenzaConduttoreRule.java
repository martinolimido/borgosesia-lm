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
public class ContrattoSenzaConduttoreRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CONTRATTO_SENZA_CONDUTTORE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() == ContrattoStato.ATTIVO && contratto.getIdConduttore() == null) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CONTRATTO_SENZA_CONDUTTORE,
                    "Contratto ATTIVO senza idConduttore valorizzato"));
        }
        return RisultatoRegola.vuoto();
    }
}
