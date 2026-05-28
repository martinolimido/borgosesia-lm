package it.borgosesiaspa.model.enums;

/**
 * Catalogo delle anomalie rilevabili dal task giornaliero di verifica di
 * {@link it.borgosesiaspa.model.ContrattoLocazione}.
 *
 * <p>Ogni codice è associato a una severità di default (vedi
 * {@link #severitaDiDefault()}) ma il valore concreto su singola
 * {@link it.borgosesiaspa.model.AnomaliaContratto} può essere differente.</p>
 */
public enum CodiceAnomalia {

    // --- Coerenza stato / date ---
    BOZZA_SCADUTA(SeveritaAnomalia.WARNING),
    CONTRATTO_ATTIVO_SENZA_DATA_INIZIO(SeveritaAnomalia.ERRORE),
    CONTRATTO_SCADUTO_NON_CESSATO(SeveritaAnomalia.ERRORE),
    CESSATO_SENZA_DATA_CESSAZIONE(SeveritaAnomalia.ERRORE),
    DATA_CESSAZIONE_FUTURA(SeveritaAnomalia.WARNING),
    DATA_FINE_PRECEDENTE_INIZIO(SeveritaAnomalia.ERRORE),

    // --- Rinnovo ---
    RINNOVO_TACITO_AL_PRIMO_DA_TRANSIRE(SeveritaAnomalia.CRITICO),
    RINNOVO_ESPRESSO_SENZA_DATA_FINE(SeveritaAnomalia.ERRORE),
    RINNOVO_TACITO_CON_DATA_FINE(SeveritaAnomalia.WARNING),
    PRIMA_SCADENZA_MANCANTE(SeveritaAnomalia.ERRORE),
    DISDETTA_IN_FINESTRA_PREAVVISO(SeveritaAnomalia.INFO),

    // --- PianoCanone ---
    CONTRATTO_ATTIVO_SENZA_PIANO_CANONE_ATTIVO(SeveritaAnomalia.ERRORE),
    PIANI_CANONE_SOVRAPPOSTI(SeveritaAnomalia.ERRORE),
    GAP_TRA_PIANI_CANONE(SeveritaAnomalia.WARNING),
    PIANO_CANONE_IMPORTO_NULLO_O_ZERO(SeveritaAnomalia.ERRORE),
    PIANO_CANONE_INCOERENTE_CON_CANONE_BASE(SeveritaAnomalia.INFO),

    // --- Canoni emessi ---
    CANONE_ATTESO_NON_GENERATO(SeveritaAnomalia.ERRORE),
    CANONE_SCADUTO_NON_INCASSATO_SENZA_MOROSITA(SeveritaAnomalia.ERRORE),
    CANONE_INCASSATO_CON_MOROSITA_APERTA(SeveritaAnomalia.ERRORE),
    CANONE_INCASSATO_MAGGIORE_IMPORTO(SeveritaAnomalia.WARNING),

    // --- Morosità ---
    MOROSITA_APERTA_OLTRE_SOGLIA(SeveritaAnomalia.WARNING),
    AZIONE_LEGALE_SENZA_MOROSITA_GRAVE(SeveritaAnomalia.INFO),

    // --- ISTAT ---
    ISTAT_RIVALUTAZIONE_SCADUTA(SeveritaAnomalia.WARNING),
    ISTAT_ATTIVA_SENZA_DATA_PROSSIMA(SeveritaAnomalia.ERRORE),
    ISTAT_PERCENTUALE_FUORI_RANGE(SeveritaAnomalia.WARNING),

    // --- Dati strutturali ---
    CONTRATTO_SENZA_UNITA(SeveritaAnomalia.ERRORE),
    CONTRATTO_SENZA_CONDUTTORE(SeveritaAnomalia.ERRORE),
    CANONE_BASE_NULLO(SeveritaAnomalia.ERRORE),
    DEPOSITO_CAUZIONALE_INCOERENTE(SeveritaAnomalia.INFO);

    private final SeveritaAnomalia severitaDiDefault;

    CodiceAnomalia(SeveritaAnomalia severitaDiDefault) {
        this.severitaDiDefault = severitaDiDefault;
    }

    public SeveritaAnomalia severitaDiDefault() {
        return severitaDiDefault;
    }
}
