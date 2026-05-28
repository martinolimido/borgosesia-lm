package it.borgosesiaspa.service.verifica;

import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;

/**
 * Anomalia rilevata da una {@link ContrattoRule} su un contratto. Forma
 * "transient": non è ancora persistita su DB; sarà il
 * {@link AnomaliaContrattoReconciler} a tradurla in
 * {@link it.borgosesiaspa.model.AnomaliaContratto}.
 *
 * <p>La {@code chiaveExtra} viene usata per costruire il fingerprint quando una
 * stessa regola può rilevare più anomalie distinte sullo stesso contratto
 * (es. due piani canone sovrapposti — la chiave include gli id dei piani).</p>
 */
public final class AnomaliaRilevata {

    private final CodiceAnomalia codice;
    private final SeveritaAnomalia severita;
    private final String chiaveExtra;
    private final String descrizione;
    private final String payloadJson;

    public AnomaliaRilevata(
            CodiceAnomalia codice,
            SeveritaAnomalia severita,
            String chiaveExtra,
            String descrizione,
            String payloadJson) {
        this.codice = codice;
        this.severita = severita;
        this.chiaveExtra = chiaveExtra;
        this.descrizione = descrizione;
        this.payloadJson = payloadJson;
    }

    public static AnomaliaRilevata di(CodiceAnomalia codice, String descrizione) {
        return new AnomaliaRilevata(codice, codice.severitaDiDefault(), null, descrizione, null);
    }

    public static AnomaliaRilevata di(CodiceAnomalia codice, String descrizione, String payloadJson) {
        return new AnomaliaRilevata(codice, codice.severitaDiDefault(), null, descrizione, payloadJson);
    }

    public static AnomaliaRilevata di(
            CodiceAnomalia codice, String chiaveExtra, String descrizione, String payloadJson) {
        return new AnomaliaRilevata(codice, codice.severitaDiDefault(), chiaveExtra, descrizione, payloadJson);
    }

    public CodiceAnomalia getCodice() {
        return codice;
    }

    public SeveritaAnomalia getSeverita() {
        return severita;
    }

    public String getChiaveExtra() {
        return chiaveExtra;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getPayloadJson() {
        return payloadJson;
    }
}
