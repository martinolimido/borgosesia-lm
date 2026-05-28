package it.borgosesiaspa.service.verifica.regole;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.StatoCanone;
import it.borgosesiaspa.model.enums.StatoMorosita;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Inverso della regola precedente: c'è una Morosità ancora aperta su un canone
 * che risulta INCASSATO. Tipicamente significa che è stata dimenticata la
 * chiusura della morosità dopo il saldo.
 */
@Component
public class CanoneIncassatoConMorositaApertaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CANONE_INCASSATO_CON_MOROSITA_APERTA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<Morosita> morosita = contratto.getMorosita() != null ? contratto.getMorosita() : List.of();
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (Morosita m : morosita) {
            if (m.getStato() != StatoMorosita.APERTA && m.getStato() != StatoMorosita.IN_SOLLECITO) {
                continue;
            }
            if (m.getCanone() == null) {
                continue;
            }
            if (m.getCanone().getStato() == StatoCanone.INCASSATO) {
                String chiave = "morosita:" + m.getId();
                String payload = "{\"morositaId\":" + m.getId() + ",\"canoneId\":"
                        + m.getCanone().getId() + ",\"statoMorosita\":\"" + m.getStato() + "\"}";
                anomalie.add(AnomaliaRilevata.di(
                        CodiceAnomalia.CANONE_INCASSATO_CON_MOROSITA_APERTA,
                        chiave,
                        "Canone id=" + m.getCanone().getId() + " INCASSATO ma Morosità id="
                                + m.getId() + " ancora " + m.getStato(),
                        payload));
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }
}
