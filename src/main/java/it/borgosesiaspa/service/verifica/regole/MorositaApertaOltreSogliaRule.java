package it.borgosesiaspa.service.verifica.regole;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.StatoMorosita;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Morosità rimaste aperte da più della soglia configurata (default 60 giorni)
 * senza essere passate a IN_SOLLECITO o promosse di livello: probabile mancanza
 * di follow-up operativo.
 */
@Component
public class MorositaApertaOltreSogliaRule implements ContrattoRule {

    private final int sogliaGiorni;

    public MorositaApertaOltreSogliaRule(
            @Value("${borgosesia.verifica-contratti.morosita.soglia-giorni-aperta:60}") int sogliaGiorni) {
        this.sogliaGiorni = sogliaGiorni;
    }

    @Override
    public String codice() {
        return CodiceAnomalia.MOROSITA_APERTA_OLTRE_SOGLIA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<Morosita> morosita = contratto.getMorosita() != null ? contratto.getMorosita() : List.of();
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (Morosita m : morosita) {
            if (m.getStato() != StatoMorosita.APERTA) {
                continue;
            }
            if (m.getDataInizio() == null) {
                continue;
            }
            long giorni = ChronoUnit.DAYS.between(m.getDataInizio(), ctx.getOggi());
            if (giorni > sogliaGiorni) {
                String chiave = "morosita:" + m.getId();
                String payload = "{\"morositaId\":" + m.getId() + ",\"dataInizio\":\""
                        + m.getDataInizio() + "\",\"giorniAperta\":" + giorni
                        + ",\"soglia\":" + sogliaGiorni + ",\"livello\":\"" + m.getLivello() + "\"}";
                anomalie.add(AnomaliaRilevata.di(
                        CodiceAnomalia.MOROSITA_APERTA_OLTRE_SOGLIA,
                        chiave,
                        "Morosità id=" + m.getId() + " aperta da " + giorni + " giorni (soglia "
                                + sogliaGiorni + ") senza promozione a IN_SOLLECITO",
                        payload));
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }
}
