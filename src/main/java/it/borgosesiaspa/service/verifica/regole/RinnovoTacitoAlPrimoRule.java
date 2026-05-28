package it.borgosesiaspa.service.verifica.regole;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.TipologiaRinnovo;
import it.borgosesiaspa.service.verifica.AnomaliaRilevata;
import it.borgosesiaspa.service.verifica.AzioneAutomatica;
import it.borgosesiaspa.service.verifica.ContestoVerifica;
import it.borgosesiaspa.service.verifica.ContrattoRule;
import it.borgosesiaspa.service.verifica.RisultatoRegola;

/**
 * Regola CHIAVE: gestisce la transizione di un contratto "tacito al primo
 * rinnovo" (es. 4+4, 6+6) verso lo stato {@code espresso} quando la
 * {@code dataPrimaScadenza} è passata e non è stata data disdetta.
 *
 * <p>L'azione automatica (se abilitata in contesto) imposta:
 * <ul>
 *   <li>{@code tipologiaRinnovo = espresso}</li>
 *   <li>{@code dataFine = dataPrimaScadenza + durataMesi}</li>
 * </ul>
 * tracciando il cambio in EventoContratto (responsabilità del chiamante).</p>
 */
@Component
public class RinnovoTacitoAlPrimoRule implements ContrattoRule {

    @Override
    public String codice() {
        return CodiceAnomalia.RINNOVO_TACITO_AL_PRIMO_DA_TRANSIRE.name();
    }

    @Override
    public RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx) {
        if (contratto.getStato() != ContrattoStato.ATTIVO) {
            return RisultatoRegola.vuoto();
        }
        if (contratto.getTipologiaRinnovo() != TipologiaRinnovo.tacito_al_primo_rinnovo) {
            return RisultatoRegola.vuoto();
        }
        LocalDate dataPrima = contratto.getDataPrimaScadenza();
        if (dataPrima == null) {
            // Caso gestito da PrimaScadenzaMancanteRule
            return RisultatoRegola.vuoto();
        }
        if (!dataPrima.isBefore(ctx.getOggi()) && !dataPrima.isEqual(ctx.getOggi())) {
            // La prima scadenza è ancora futura
            return RisultatoRegola.vuoto();
        }
        Integer durataMesi = contratto.getDurataMesi();
        if (durataMesi == null || durataMesi <= 0) {
            // Senza durataMesi non possiamo calcolare la nuova dataFine: solo log.
            return RisultatoRegola.anomalia(AnomaliaRilevata.di(
                    CodiceAnomalia.RINNOVO_TACITO_AL_PRIMO_DA_TRANSIRE,
                    "Contratto tacito_al_primo_rinnovo con dataPrimaScadenza " + dataPrima
                            + " già passata, ma durataMesi non valorizzata: transizione manuale richiesta"));
        }

        LocalDate nuovaDataFine = dataPrima.plusMonths(durataMesi);
        String payload = "{\"dataPrimaScadenza\":\"" + dataPrima + "\",\"durataMesi\":" + durataMesi
                + ",\"nuovaDataFine\":\"" + nuovaDataFine + "\"}";

        AnomaliaRilevata anomalia = AnomaliaRilevata.di(
                CodiceAnomalia.RINNOVO_TACITO_AL_PRIMO_DA_TRANSIRE,
                "Contratto tacito_al_primo_rinnovo con dataPrimaScadenza " + dataPrima
                        + " passata: transizione a ESPRESSO con nuova dataFine " + nuovaDataFine,
                payload);

        if (!ctx.isAzioniAutomaticheAbilitate()) {
            return RisultatoRegola.anomalia(anomalia);
        }

        AzioneAutomatica azione = new AzioneAutomatica() {
            @Override
            public String codice() {
                return "TRANSIZIONE_RINNOVO_ESPRESSO";
            }

            @Override
            public String descrizione() {
                return "Transizione tacito_al_primo_rinnovo -> espresso con nuova dataFine "
                        + nuovaDataFine;
            }

            @Override
            public String applica(ContrattoLocazione c) {
                TipologiaRinnovo prevRinnovo = c.getTipologiaRinnovo();
                LocalDate prevDataFine = c.getDataFine();
                c.setTipologiaRinnovo(TipologiaRinnovo.espresso);
                c.setDataFine(nuovaDataFine);
                return "{\"before\":{\"tipologiaRinnovo\":\"" + prevRinnovo + "\",\"dataFine\":\""
                        + prevDataFine + "\"},\"after\":{\"tipologiaRinnovo\":\"espresso\",\"dataFine\":\""
                        + nuovaDataFine + "\"},\"dataPrimaScadenza\":\"" + dataPrima
                        + "\",\"durataMesi\":" + durataMesi + "}";
            }
        };
        return RisultatoRegola.anomaliaConAzione(anomalia, azione);
    }
}
