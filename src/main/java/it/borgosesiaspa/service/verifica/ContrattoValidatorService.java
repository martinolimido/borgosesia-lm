package it.borgosesiaspa.service.verifica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.borgosesiaspa.model.ContrattoLocazione;

/**
 * Orchestratore: applica tutte le {@link ContrattoRule} registrate al contratto
 * e raccoglie i risultati. Non scrive su DB; non muta il contratto. La
 * persistenza delle anomalie e l'esecuzione delle azioni automatiche sono
 * responsabilità di {@link AnomaliaContrattoReconciler}.
 */
@Service
public class ContrattoValidatorService {

    private static final Logger log = LoggerFactory.getLogger(ContrattoValidatorService.class);

    private final List<ContrattoRule> regole;

    public ContrattoValidatorService(List<ContrattoRule> regole) {
        // Ordiniamo per nome di classe per avere un ordine deterministico nei log.
        List<ContrattoRule> copia = new ArrayList<>(regole);
        copia.sort((a, b) -> a.getClass().getSimpleName().compareTo(b.getClass().getSimpleName()));
        this.regole = Collections.unmodifiableList(copia);
        log.info("ContrattoValidatorService inizializzato con {} regole: {}", regole.size(),
                regole.stream().map(r -> r.getClass().getSimpleName()).toList());
    }

    /**
     * Esegue tutte le regole sul contratto e ritorna un risultato aggregato.
     */
    public RisultatoValidazione valida(ContrattoLocazione contratto, ContestoVerifica ctx) {
        List<AnomaliaRilevata> anomalie = new ArrayList<>();
        List<AzioneAutomatica> azioni = new ArrayList<>();
        for (ContrattoRule r : regole) {
            try {
                RisultatoRegola res = r.applica(contratto, ctx);
                if (res != null && !res.isVuoto()) {
                    anomalie.addAll(res.getAnomalie());
                    azioni.addAll(res.getAzioni());
                }
            } catch (RuntimeException e) {
                // Una regola difettosa non deve far fallire l'intero run.
                log.error("Errore applicando la regola {} al contratto id={}: {}",
                        r.getClass().getSimpleName(),
                        contratto != null ? contratto.getId() : null,
                        e.getMessage(), e);
            }
        }
        return new RisultatoValidazione(anomalie, azioni);
    }

    public int numeroRegole() {
        return regole.size();
    }
}
