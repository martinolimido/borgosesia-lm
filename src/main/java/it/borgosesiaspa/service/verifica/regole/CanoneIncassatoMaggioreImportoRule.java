package it.borgosesiaspa.service.verifica.regole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class CanoneIncassatoMaggioreImportoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.CANONE_INCASSATO_MAGGIORE_IMPORTO.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<Canone> canoni = contratto.getCanoni() != null ? contratto.getCanoni() : List.of();
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        for (Canone c : canoni) {
            if (c.getImporto() == null || c.getImportoIncassato() == null) {
                continue;
            }
            if (c.getImportoIncassato().compareTo(c.getImporto()) > 0) {
                String chiave = "canone:" + c.getId();
                BigDecimal eccedenza = c.getImportoIncassato().subtract(c.getImporto());
                String payload = "{\"canoneId\":" + c.getId() + ",\"importo\":" + c.getImporto()
                        + ",\"importoIncassato\":" + c.getImportoIncassato() + ",\"eccedenza\":"
                        + eccedenza + "}";
                anomalie.add(AnomaliaRilevata.di(
                        CodiceAnomalia.CANONE_INCASSATO_MAGGIORE_IMPORTO,
                        chiave,
                        "Canone id=" + c.getId() + ": importoIncassato (" + c.getImportoIncassato()
                                + ") supera importo (" + c.getImporto() + ") di " + eccedenza,
                        payload));
            }
        }
        return RisultatoRegola.anomalie(anomalie);
    }
}
