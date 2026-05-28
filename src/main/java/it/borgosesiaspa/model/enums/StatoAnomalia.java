package it.borgosesiaspa.model.enums;

/**
 * Stato del ciclo di vita di un'anomalia rilevata sul contratto.
 *
 * <ul>
 *   <li>APERTA — l'anomalia è stata rilevata ed è ancora presente.</li>
 *   <li>RISOLTA — il task ha verificato che la condizione di anomalia non si verifica più.</li>
 *   <li>IGNORATA — l'operatore ha deciso di silenziare l'anomalia (falso positivo o accettata).</li>
 * </ul>
 */
public enum StatoAnomalia {
    APERTA,
    RISOLTA,
    IGNORATA
}
