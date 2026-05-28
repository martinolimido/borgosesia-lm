package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class PianoCanoneImportoNulloOZeroRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.PIANO_CANONE_IMPORTO_NULLO_O_ZERO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (PianoCanone p : PianoCanoneUtil.pianiVivi(contratto)) {
            if (!PianoCanoneUtil.attivoAllaData(p, ctx.getOggi())) {
                continue;
            }
            if (p.getImporto() == null || p.getImporto().compareTo(BigDecimal.ZERO) <= 0) {
                String chiave = "piano:" + p.getId();
                String payload = "{\"pianoCanoneId\":" + p.getId() + ",\"importo\":"
                        + (p.getImporto() == null ? "null" : p.getImporto()) + "}";
                anomalie.add(AnomaliaRilevata.di(
                        CodiceAnomalia.PIANO_CANONE_IMPORTO_NULLO_O_ZERO,
                        chiave,
                        "Piano canone id=" + p.getId() + " attivo con importo "
                                + (p.getImporto() == null ? "null" : p.getImporto()),
                        payload));
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }
}
