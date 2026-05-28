package it.borgosesiaspa.service.verifica.regole;

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
public class PianiCanoneSovrappostiRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.PIANI_CANONE_SOVRAPPOSTI.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<PianoCanone> piani = PianoCanoneUtil.pianiVivi(contratto);
        if (piani.size() < 2) {
            return RisultatoRegola.vuoto();
        }
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (int i = 0; i < piani.size(); i++) {
            for (int j = i + 1; j < piani.size(); j++) {
                PianoCanone a = piani.get(i);
                PianoCanone b = piani.get(j);
                if (PianoCanoneUtil.periodoSiSovrappone(a, b)) {
                    String chiave = "piani:" + Math.min(idLong(a), idLong(b)) + "-"
                            + Math.max(idLong(a), idLong(b));
                    String payload = "{\"pianoA\":{\"id\":" + a.getId() + ",\"da\":\""
                            + a.getDataInizioValidita() + "\",\"a\":\"" + a.getDataFineValidita()
                            + "\"},\"pianoB\":{\"id\":" + b.getId() + ",\"da\":\""
                            + b.getDataInizioValidita() + "\",\"a\":\"" + b.getDataFineValidita() + "\"}}";
                    anomalie.add(AnomaliaRilevata.di(
                            CodiceAnomalia.PIANI_CANONE_SOVRAPPOSTI,
                            chiave,
                            "Piani canone sovrapposti: id=" + a.getId() + " e id=" + b.getId(),
                            payload));
                }
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }

    private long idLong(PianoCanone p) {
        return p.getId() != null ? p.getId() : 0L;
    }
}
