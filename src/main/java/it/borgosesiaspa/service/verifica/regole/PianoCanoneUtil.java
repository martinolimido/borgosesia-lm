package it.borgosesiaspa.service.verifica.regole;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.PianoCanone;

/**
 * Utility condivise tra le regole che lavorano sui {@link PianoCanone}.
 */
final class PianoCanoneUtil {

    private PianoCanoneUtil() {
    }

    /**
     * Restituisce i piani considerati "vivi" — non annullati. Sono i piani
     * candidati a essere attivi in qualche istante della vita del contratto.
     */
    static List<PianoCanone> pianiVivi(ContrattoLocazione c) {
        if (c.getPianiCanone() == null) {
            return List.of();
        }
        return c.getPianiCanone().stream()
                .filter(p -> p.getDataAnnullamento() == null)
                .collect(Collectors.toList());
    }

    /**
     * True se il piano è attivo alla data indicata: inizio &lt;= data
     * &lt;= fine (o fine null = aperto).
     */
    static boolean attivoAllaData(PianoCanone p, LocalDate data) {
        if (p.getDataInizioValidita() == null) {
            return false;
        }
        if (data.isBefore(p.getDataInizioValidita())) {
            return false;
        }
        if (p.getDataFineValidita() != null && data.isAfter(p.getDataFineValidita())) {
            return false;
        }
        return true;
    }

    /**
     * True se i periodi di validità di due piani si intersecano.
     * Periodi half-open con fine inclusiva (giorno per giorno).
     */
    static boolean periodoSiSovrappone(PianoCanone a, PianoCanone b) {
        if (a.getDataInizioValidita() == null || b.getDataInizioValidita() == null) {
            return false;
        }
        LocalDate aFine = a.getDataFineValidita() != null ? a.getDataFineValidita() : LocalDate.MAX;
        LocalDate bFine = b.getDataFineValidita() != null ? b.getDataFineValidita() : LocalDate.MAX;
        return !a.getDataInizioValidita().isAfter(bFine)
                && !b.getDataInizioValidita().isAfter(aFine);
    }
}
