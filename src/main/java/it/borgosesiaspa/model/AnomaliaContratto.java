package it.borgosesiaspa.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;
import it.borgosesiaspa.model.enums.StatoAnomalia;
import it.borgosesiaspa.shared.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Log persistente delle anomalie rilevate dal task giornaliero di verifica
 * sui contratti di locazione.
 *
 * <p>Ogni record rappresenta un'occorrenza di anomalia su un contratto. Il task
 * giornaliero usa il {@link #hashFingerprint} per riconciliare i risultati dei
 * vari run: se al run successivo la stessa anomalia non si presenta più, lo
 * stato viene portato a {@link StatoAnomalia#RISOLTA}; se è ancora presente, il
 * record esistente viene aggiornato (non se ne crea uno nuovo).</p>
 */
@Entity
@Table(indexes = {
        @Index(name = "idx_anomalia_idContratto", columnList = "id_contratto_locazione"),
        @Index(name = "idx_anomalia_stato", columnList = "stato"),
        @Index(name = "idx_anomalia_codice", columnList = "codice"),
        @Index(name = "idx_anomalia_fingerprint", columnList = "hashFingerprint")
})
public class AnomaliaContratto extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
    private ContrattoLocazione contrattoLocazione;

    @Column(nullable = false, length = 64)
    @Enumerated(EnumType.STRING)
    private CodiceAnomalia codice;

    @Column(nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private SeveritaAnomalia severita;

    @Column(nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private StatoAnomalia stato;

    /**
     * Hash deterministico costruito dal Reconciler a partire dal contratto,
     * codice anomalia ed eventuali chiavi aggiuntive (es. id piano canone
     * coinvolto). Serve per evitare duplicati nei run successivi.
     */
    @Column(nullable = false, length = 64)
    private String hashFingerprint;

    @Column(nullable = false)
    private LocalDateTime dataRilevazione;

    @Column
    private LocalDateTime dataUltimoRiscontro;

    @Column
    private LocalDateTime dataRisoluzione;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    /**
     * Note libere dell'operatore (es. motivazione di {@link StatoAnomalia#IGNORATA}).
     */
    @Column(columnDefinition = "TEXT")
    private String noteOperatore;

    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }

    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
    }

    public CodiceAnomalia getCodice() {
        return codice;
    }

    public void setCodice(CodiceAnomalia codice) {
        this.codice = codice;
    }

    public SeveritaAnomalia getSeverita() {
        return severita;
    }

    public void setSeverita(SeveritaAnomalia severita) {
        this.severita = severita;
    }

    public StatoAnomalia getStato() {
        return stato;
    }

    public void setStato(StatoAnomalia stato) {
        this.stato = stato;
    }

    public String getHashFingerprint() {
        return hashFingerprint;
    }

    public void setHashFingerprint(String hashFingerprint) {
        this.hashFingerprint = hashFingerprint;
    }

    public LocalDateTime getDataRilevazione() {
        return dataRilevazione;
    }

    public void setDataRilevazione(LocalDateTime dataRilevazione) {
        this.dataRilevazione = dataRilevazione;
    }

    public LocalDateTime getDataUltimoRiscontro() {
        return dataUltimoRiscontro;
    }

    public void setDataUltimoRiscontro(LocalDateTime dataUltimoRiscontro) {
        this.dataUltimoRiscontro = dataUltimoRiscontro;
    }

    public LocalDateTime getDataRisoluzione() {
        return dataRisoluzione;
    }

    public void setDataRisoluzione(LocalDateTime dataRisoluzione) {
        this.dataRisoluzione = dataRisoluzione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getNoteOperatore() {
        return noteOperatore;
    }

    public void setNoteOperatore(String noteOperatore) {
        this.noteOperatore = noteOperatore;
    }
}
