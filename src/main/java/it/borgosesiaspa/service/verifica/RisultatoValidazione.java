package it.borgosesiaspa.service.verifica;

import java.util.List;

/**
 * Aggregato delle anomalie e azioni prodotte dall'applicazione di tutte le
 * {@link ContrattoRule} su un singolo contratto.
 */
public final class RisultatoValidazione {

    private final List<AnomaliaRilevata> anomalie;
    private final List<AzioneAutomatica> azioni;

    public RisultatoValidazione(List<AnomaliaRilevata> anomalie, List<AzioneAutomatica> azioni) {
        this.anomalie = List.copyOf(anomalie);
        this.azioni = List.copyOf(azioni);
    }

    public List<AnomaliaRilevata> getAnomalie() {
        return anomalie;
    }

    public List<AzioneAutomatica> getAzioni() {
        return azioni;
    }
}
