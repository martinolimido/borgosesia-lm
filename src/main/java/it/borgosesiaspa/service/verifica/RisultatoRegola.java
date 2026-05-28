package it.borgosesiaspa.service.verifica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Risultato dell'applicazione di una {@link ContrattoRule} su un contratto.
 *
 * <p>Una regola può ritornare zero o più anomalie e zero o più azioni
 * automatiche da eseguire. Il caso più comune è "nessuna anomalia" — in tal
 * caso si usa {@link #vuoto()}.</p>
 */
public final class RisultatoRegola {

    private static final RisultatoRegola VUOTO = new RisultatoRegola(
            Collections.emptyList(), Collections.emptyList());

    private final List<AnomaliaRilevata> anomalie;
    private final List<AzioneAutomatica> azioni;

    private RisultatoRegola(List<AnomaliaRilevata> anomalie, List<AzioneAutomatica> azioni) {
        this.anomalie = anomalie;
        this.azioni = azioni;
    }

    public static RisultatoRegola vuoto() {
        return VUOTO;
    }

    public static RisultatoRegola anomalia(AnomaliaRilevata anomalia) {
        return new RisultatoRegola(List.of(anomalia), Collections.emptyList());
    }

    public static RisultatoRegola anomalie(List<AnomaliaRilevata> anomalie) {
        if (anomalie == null || anomalie.isEmpty()) {
            return VUOTO;
        }
        return new RisultatoRegola(List.copyOf(anomalie), Collections.emptyList());
    }

    public static RisultatoRegola anomaliaConAzione(AnomaliaRilevata anomalia, AzioneAutomatica azione) {
        return new RisultatoRegola(List.of(anomalia), List.of(azione));
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<AnomaliaRilevata> getAnomalie() {
        return anomalie;
    }

    public List<AzioneAutomatica> getAzioni() {
        return azioni;
    }

    public boolean isVuoto() {
        return anomalie.isEmpty() && azioni.isEmpty();
    }

    public static final class Builder {
        private final List<AnomaliaRilevata> anomalie = new ArrayList<>();
        private final List<AzioneAutomatica> azioni = new ArrayList<>();

        public Builder aggiungiAnomalia(AnomaliaRilevata a) {
            if (a != null) {
                anomalie.add(a);
            }
            return this;
        }

        public Builder aggiungiAzione(AzioneAutomatica a) {
            if (a != null) {
                azioni.add(a);
            }
            return this;
        }

        public RisultatoRegola build() {
            if (anomalie.isEmpty() && azioni.isEmpty()) {
                return VUOTO;
            }
            return new RisultatoRegola(List.copyOf(anomalie), List.copyOf(azioni));
        }
    }
}
