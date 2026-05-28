package it.borgosesiaspa.service.verifica.regole;

import java.util.List;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.LivelloMorosita;
import it.borgosesiaspa.model.enums.StatoMorosita;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

@Component
public class AzioneLegaleSenzaMorositaGraveRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.AZIONE_LEGALE_SENZA_MOROSITA_GRAVE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (!Boolean.TRUE.equals(contratto.getAzioneLegaleInCorso())) {
            return RisultatoRegola.vuoto();
        }
        List<Morosita> morosita = contratto.getMorosita() != null ? contratto.getMorosita() : List.of();
        boolean haMorositaGrave = morosita.stream()
                .anyMatch(m -> m.getLivello() == LivelloMorosita.GRAVE
                        && (m.getStato() == StatoMorosita.APERTA
                                || m.getStato() == StatoMorosita.IN_SOLLECITO));
        if (!haMorositaGrave) {
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.AZIONE_LEGALE_SENZA_MOROSITA_GRAVE,
                    "Contratto con azione legale in corso ma nessuna Morosità di livello GRAVE aperta"));
        }
        return RisultatoRegola.vuoto();
    }
}
