package it.borgosesiaspa.service.verifica;

import it.borgosesiaspa.model.ContrattoLocazione;

/**
 * Azione correttiva automatica applicabile a un contratto, prodotta da una
 * {@link ContrattoRule} quando il task giornaliero deve correggere lo stato di
 * un contratto (oltre a loggare l'anomalia).
 *
 * <p>L'azione viene eseguita dal validator/reconciler solo se
 * {@link ContestoVerifica#isAzioniAutomaticheAbilitate()} è true. Ogni azione
 * deve produrre una riga in {@link it.borgosesiaspa.model.EventoContratto} per
 * tracciabilità (responsabilità delegata al chiamante).</p>
 */
public interface AzioneAutomatica {

    /**
     * Identificatore stabile dell'azione (usato per i log strutturati).
     * Esempio: {@code "TRANSIZIONE_RINNOVO_ESPRESSO"}.
     */
    String codice();

    /**
     * Descrizione user-facing dell'azione, per il payloadJson dell'evento.
     */
    String descrizione();

    /**
     * Applica l'azione modificando il contratto. Ritorna un JSON che rappresenta
     * lo snapshot pre/post (verrà salvato nel payloadJson dell'EventoContratto).
     */
    String applica(ContrattoLocazione contratto);
}
