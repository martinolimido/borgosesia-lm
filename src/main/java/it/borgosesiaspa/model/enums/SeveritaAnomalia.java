package it.borgosesiaspa.model.enums;

/**
 * Livello di severità di una {@link it.borgosesiaspa.model.AnomaliaContratto}.
 * Usato dal task giornaliero di verifica per classificare i problemi rilevati.
 */
public enum SeveritaAnomalia {
    INFO,
    WARNING,
    ERRORE,
    CRITICO
}
