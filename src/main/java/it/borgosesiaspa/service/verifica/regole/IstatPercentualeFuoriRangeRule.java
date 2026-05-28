package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * La percentuale ISTAT applicabile è in pratica nell'intervallo [0, 100] —
 * percentuali fuori range indicano quasi certamente un errore di inserimento.
 */
@Component
public class IstatPercentualeFuoriRangeRule implements ContrattoRule {

    private static final BigDecimal MIN = BigDecimal.ZERO;
    private static final BigDecimal MAX = new BigDecimal("100");

    @Override
    public String codice() {
        return CodiceAnomalia.ISTAT_PERCENTUALE_FUORI_RANGE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        BigDecimal perc = contratto.getPercentualeIstat();
        if (perc == null) {
            return RisultatoRegola.vuoto();
        }
        if (perc.compareTo(MIN) < 0 || perc.compareTo(MAX) > 0) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.ISTAT_PERCENTUALE_FUORI_RANGE,
                    "percentualeIstat=" + perc + " fuori range [0, 100]"));
        }
        return RisultatoRegola.vuoto();
    }
}
