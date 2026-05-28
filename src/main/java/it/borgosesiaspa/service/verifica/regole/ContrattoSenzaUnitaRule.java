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
public class ContrattoSenzaUnitaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CONTRATTO_SENZA_UNITA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getUnita() == null || contratto.getUnita().isEmpty()) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CONTRATTO_SENZA_UNITA,
                    "Contratto ATTIVO senza alcuna unità associata"));
        }
        return RisultatoRegola.vuoto();
    }
}
