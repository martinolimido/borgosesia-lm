package it.borgosesiaspa.service.verifica.regole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Cerca buchi temporali (gap) tra piani canone consecutivi durante la vita
 * (passata, fino a oggi) di un contratto attivo. Non considera gap futuri.
 */
@Component
public class GapTraPianiCanoneRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.GAP_TRA_PIANI_CANONE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        List<PianoCanone> piani = new ArrayList<>(PianoCanoneUtil.pianiVivi(contratto));
        piani.removeIf(p -> p.getDataInizioValidita() == null);
        if (piani.size() < 2) {
            return RisultatoRegola.vuoto();
        }
        piani.sort(Comparator.comparing(PianoCanone::getDataInizioValidita));
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (int i = 0; i < piani.size() - 1; i++) {
            PianoCanone corrente = piani.get(i);
            PianoCanone successivo = piani.get(i + 1);
            LocalDate fineCorrente = corrente.getDataFineValidita();
            if (fineCorrente == null) {
                // piano aperto: per definizione non lascia gap
                continue;
            }
            LocalDate gapInizio = fineCorrente.plusDays(1);
            if (gapInizio.isBefore(successivo.getDataInizioValidita())
                    && !gapInizio.isAfter(ctx.getOggi())) {
                String chiave = "gap:" + corrente.getId() + "-" + successivo.getId();
                String payload = "{\"fineCorrente\":\"" + fineCorrente + "\",\"inizioSuccessivo\":\""
                        + successivo.getDataInizioValidita() + "\"}";
                anomalie.add(AnomaliaRilevata.di(
                        CodiceAnomalia.GAP_TRA_PIANI_CANONE,
                        chiave,
                        "Gap tra piano " + corrente.getId() + " (fine " + fineCorrente
                                + ") e piano " + successivo.getId() + " (inizio "
                                + successivo.getDataInizioValidita() + ")",
                        payload));
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }
}
