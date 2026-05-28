package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.Canone;
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
 * Segnala canoni scaduti/insoluti che non hanno una Morosità collegata e
 * aperta — indica un disallineamento del workflow di apertura morosità.
 */
@Component
public class CanoneScadutoNonIncassatoSenzaMorositaRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CANONE_SCADUTO_NON_INCASSATO_SENZA_MOROSITA.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<Canone> canoni = contratto.getCanoni() != null ? contratto.getCanoni() : List.of();
        List<Morosita> morositaAperte = (contratto.getMorosita() != null
                ? contratto.getMorosita()
                : List.<Morosita>of())
                .stream()
                .filter(m -> m.getStato() == StatoMorosita.APERTA
                        || m.getStato() == StatoMorosita.IN_SOLLECITO)
                .toList();
        Set<Long> canoniConMorositaAperta = morositaAperte.stream()
                .filter(m -> m.getCanone() != null && m.getCanone().getId() != null)
                .map(m -> m.getCanone().getId())
                .collect(Collectors.toSet());

        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (Canone c : canoni) {
            if (!isCanoneInRitardo(c, ctx)) {
                continue;
            }
            if (c.getId() != null && canoniConMorositaAperta.contains(c.getId())) {
                continue;
            }
            BigDecimal residuo = residuo(c);
            String chiave = "canone:" + c.getId();
            String payload = "{\"canoneId\":" + c.getId() + ",\"importo\":" + c.getImporto()
                    + ",\"importoIncassato\":" + (c.getImportoIncassato() != null ? c.getImportoIncassato() : "0")
                    + ",\"residuo\":" + residuo + ",\"scadenza\":\"" + c.getDataScadenza() + "\"}";
            anomalie.add(AnomaliaRilevata.di(
                    CodiceAnomalia.CANONE_SCADUTO_NON_INCASSATO_SENZA_MOROSITA,
                    chiave,
                    "Canone id=" + c.getId() + " scaduto/insoluto con residuo " + residuo
                            + " senza Morosità aperta collegata",
                    payload));
        }
        return RisultatoRegola.anomalie(anomalie);
    }

    private boolean isCanoneInRitardo(Canone c, ContestoVerifica ctx) {
        if (c.getStato() == StatoCanone.ANNULLATO || c.getStato() == StatoCanone.INCASSATO) {
            return false;
        }
        BigDecimal residuo = residuo(c);
        if (residuo.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (c.getStato() == StatoCanone.INSOLUTO || c.getStato() == StatoCanone.SCADUTO) {
            return true;
        }
        // EMESSO o PARZIALMENTE_INCASSATO: considera scaduto se dataScadenza < oggi
        return c.getDataScadenza() != null && c.getDataScadenza().isBefore(ctx.getOggi());
    }

    private BigDecimal residuo(Canone c) {
        BigDecimal importo = c.getImporto() != null ? c.getImporto() : BigDecimal.ZERO;
        BigDecimal incassato = c.getImportoIncassato() != null ? c.getImportoIncassato() : BigDecimal.ZERO;
        return importo.subtract(incassato);
    }
}
