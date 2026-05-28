package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Verifica che il primo piano canone (più vecchio per dataInizioValidità)
 * abbia importo coerente con il canoneBase del contratto. Tolleranza di 1%.
 */
@Component
public class PianoCanoneIncoerenteConCanoneBaseRule implements ContrattoRule {

    private static final BigDecimal TOLLERANZA = new BigDecimal("0.01");

    @Override
    public String codice() {
        return CodiceAnomalia.PIANO_CANONE_INCOERENTE_CON_CANONE_BASE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        BigDecimal canoneBase = contratto.getCanoneBase();
        if (canoneBase == null || canoneBase.compareTo(BigDecimal.ZERO) == 0) {
            return RisultatoRegola.vuoto();
        }
        List<PianoCanone> piani = PianoCanoneUtil.pianiVivi(contratto);
        Optional<PianoCanone> primo = piani.stream()
                .filter(p -> p.getDataInizioValidita() != null)
                .min(Comparator.comparing(PianoCanone::getDataInizioValidita));
        if (primo.isEmpty()) {
            return RisultatoRegola.vuoto();
        }
        PianoCanone p = primo.get();
        if (p.getImporto() == null) {
            return RisultatoRegola.vuoto();
        }
        BigDecimal diff = p.getImporto().subtract(canoneBase).abs();
        BigDecimal sogliaAssoluta = canoneBase.abs().multiply(TOLLERANZA).setScale(2, RoundingMode.HALF_UP);
        if (diff.compareTo(sogliaAssoluta) > 0) {
            String chiave = "piano:" + p.getId();
            String payload = "{\"canoneBase\":" + canoneBase + ",\"importoPrimoPiano\":" + p.getImporto()
                    + ",\"diff\":" + diff + ",\"sogliaAssoluta\":" + sogliaAssoluta + "}";
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.PIANO_CANONE_INCOERENTE_CON_CANONE_BASE,
                    chiave,
                    "Importo primo piano canone (" + p.getImporto() + ") incoerente con canoneBase ("
                            + canoneBase + "), differenza " + diff,
                    payload));
        }
        return RisultatoRegola.vuoto();
    }
}
