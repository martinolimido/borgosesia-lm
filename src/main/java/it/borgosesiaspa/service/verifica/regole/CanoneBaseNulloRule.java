package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class CanoneBaseNulloRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CANONE_BASE_NULLO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        BigDecimal canoneBase = contratto.getCanoneBase();
        if (canoneBase == null || canoneBase.compareTo(BigDecimal.ZERO) <= 0) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CANONE_BASE_NULLO,
                    "Contratto ATTIVO con canoneBase " + (canoneBase == null ? "null" : canoneBase)));
        }
        return RisultatoRegola.vuoto();
    }
}
