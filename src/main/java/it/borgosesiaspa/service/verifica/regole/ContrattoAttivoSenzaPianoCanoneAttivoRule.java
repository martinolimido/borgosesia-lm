package it.borgosesiaspa.service.verifica.regole;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Regola fondamentale: un contratto ATTIVO deve avere almeno un PianoCanone
 * attivo alla data odierna (e non annullato).
 */
@Component
public class ContrattoAttivoSenzaPianoCanoneAttivoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CONTRATTO_ATTIVO_SENZA_PIANO_CANONE_ATTIVO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        boolean haPianoAttivo = PianoCanoneUtil.pianiVivi(contratto).stream()
                .anyMatch(p -> PianoCanoneUtil.attivoAllaData(p, ctx.getOggi()));
        if (!haPianoAttivo) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.CONTRATTO_ATTIVO_SENZA_PIANO_CANONE_ATTIVO,
                    "Contratto in stato ATTIVO senza alcun PianoCanone attivo alla data "
                            + ctx.getOggi()));
        }
        return RisultatoRegola.vuoto();
    }
}
