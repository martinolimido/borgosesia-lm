package it.borgosesiaspa.service.verifica;

import java.time.LocalDateTime;

/**
 * Riassunto del risultato di un singolo run del task di verifica contratti.
 * Esposto via REST per consentire trigger manuali da admin.
 */
public final class RisultatoRun {

    private final LocalDateTime inizio;
    private final LocalDateTime fine;
    private final int contrattiAnalizzati;
    private final int anomalieAperte;
    private final int anomalieAggiornate;
    private final int anomalieRisolte;
    private final int azioniEseguite;
    private final int erroriContratto;

    public RisultatoRun(
            LocalDateTime inizio,
            LocalDateTime fine,
            int contrattiAnalizzati,
            int anomalieAperte,
            int anomalieAggiornate,
            int anomalieRisolte,
            int azioniEseguite,
            int erroriContratto) {
        this.inizio = inizio;
        this.fine = fine;
        this.contrattiAnalizzati = contrattiAnalizzati;
        this.anomalieAperte = anomalieAperte;
        this.anomalieAggiornate = anomalieAggiornate;
        this.anomalieRisolte = anomalieRisolte;
        this.azioniEseguite = azioniEseguite;
        this.erroriContratto = erroriContratto;
    }

    public LocalDateTime getInizio() {
        return inizio;
    }

    public LocalDateTime getFine() {
        return fine;
    }

    public int getContrattiAnalizzati() {
        return contrattiAnalizzati;
    }

    public int getAnomalieAperte() {
        return anomalieAperte;
    }

    public int getAnomalieAggiornate() {
        return anomalieAggiornate;
    }

    public int getAnomalieRisolte() {
        return anomalieRisolte;
    }

    public int getAzioniEseguite() {
        return azioniEseguite;
    }

    public int getErroriContratto() {
        return erroriContratto;
    }
}
