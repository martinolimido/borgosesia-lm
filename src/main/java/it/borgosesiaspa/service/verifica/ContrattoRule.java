package it.borgosesiaspa.service.verifica;

import it.borgosesiaspa.model.ContrattoLocazione;

/**
 * Singola regola di verifica eseguita dal task giornaliero su un contratto.
 *
 * <p>Le implementazioni devono essere stateless (Spring beans, prototype-safe),
 * non chiamare il DB e non mutare il contratto: la mutazione, quando richiesta,
 * avviene tramite {@link AzioneAutomatica} restituita nel
 * {@link RisultatoRegola}.</p>
 */
public interface ContrattoRule {

    /**
     * Codice identificativo della regola, usato nei log strutturati. Per
     * convenzione coincide con il nome della classe in snake_upper (es.
     * {@code CONTRATTO_SCADUTO_NON_CESSATO}).
     */
    String codice();

    /**
     * Valuta la regola sul contratto. Deve essere idempotente: invocare due
     * volte con lo stesso contratto e contesto deve produrre lo stesso
     * risultato. Non lanciare eccezioni: usa
     * {@link RisultatoRegola#vuoto()} se non applicabile.
     */
    RisultatoRegola applica(ContrattoLocazione contratto, ContestoVerifica ctx);
}
