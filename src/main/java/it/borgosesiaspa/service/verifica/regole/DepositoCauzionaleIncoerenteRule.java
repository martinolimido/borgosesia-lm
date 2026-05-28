package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Segnala come INFO un deposito cauzionale superiore alla soglia configurata
 * (default 3 mensilità di canone base). Soglia regolabile.
 */
@Component
public class DepositoCauzionaleIncoerenteRule implements ContrattoRule {

    private final int massimoMensilita;

    public DepositoCauzionaleIncoerenteRule(
            @Value("${borgosesia.verifica-contratti.deposito.massimo-mensilita:3}") int massimoMensilita) {
        this.massimoMensilita = massimoMensilita;
    }

    @Override
    public String codice() {
        return CodiceAnomalia.DEPOSITO_CAUZIONALE_INCOERENTE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        BigDecimal deposito = contratto.getDepositoCauzionale();
        BigDecimal canoneBase = contratto.getCanoneBase();
        if (deposito == null || canoneBase == null || canoneBase.compareTo(BigDecimal.ZERO) <= 0) {
            return RisultatoRegola.vuoto();
        }
        BigDecimal soglia = canoneBase.multiply(BigDecimal.valueOf(massimoMensilita));
        if (deposito.compareTo(soglia) > 0) {
            String payload = "{\"deposito\":" + deposito + ",\"canoneBase\":" + canoneBase
                    + ",\"massimoMensilita\":" + massimoMensilita + ",\"soglia\":" + soglia + "}";
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.DEPOSITO_CAUZIONALE_INCOERENTE,
                    "Deposito cauzionale (" + deposito + ") superiore a " + massimoMensilita
                            + " mensilità del canone base (" + canoneBase + ")",
                    payload));
        }
        return RisultatoRegola.vuoto();
    }
}
