package it.borgosesiaspa.dto.read;

import java.time.LocalDateTime;

import it.borgosesiaspa.model.AnomaliaContratto;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;
import it.borgosesiaspa.model.enums.StatoAnomalia;

/**
 * DTO di lettura per {@link AnomaliaContratto}.
 */
public class AnomaliaContrattoReadDto {

    private Long id;
    private Long contrattoLocazioneId;
    private String contrattoLocazioneCodice;
    private CodiceAnomalia codice;
    private SeveritaAnomalia severita;
    private StatoAnomalia stato;
    private String hashFingerprint;
    private LocalDateTime dataRilevazione;
    private LocalDateTime dataUltimoRiscontro;
    private LocalDateTime dataRisoluzione;
    private String descrizione;
    private String payloadJson;
    private String noteOperatore;

    public AnomaliaContrattoReadDto() {
    }

    public static AnomaliaContrattoReadDto fromEntity(AnomaliaContratto a) {
        if (a == null) {
            return null;
        }
        AnomaliaContrattoReadDto dto = new AnomaliaContrattoReadDto();
        dto.id = a.getId();
        if (a.getContrattoLocazione() != null) {
            dto.contrattoLocazioneId = a.getContrattoLocazione().getId();
            dto.contrattoLocazioneCodice = a.getContrattoLocazione().getCodiceContratto();
        }
        dto.codice = a.getCodice();
        dto.severita = a.getSeverita();
        dto.stato = a.getStato();
        dto.hashFingerprint = a.getHashFingerprint();
        dto.dataRilevazione = a.getDataRilevazione();
        dto.dataUltimoRiscontro = a.getDataUltimoRiscontro();
        dto.dataRisoluzione = a.getDataRisoluzione();
        dto.descrizione = a.getDescrizione();
        dto.payloadJson = a.getPayloadJson();
        dto.noteOperatore = a.getNoteOperatore();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContrattoLocazioneId() {
        return contrattoLocazioneId;
    }

    public void setContrattoLocazioneId(Long contrattoLocazioneId) {
        this.contrattoLocazioneId = contrattoLocazioneId;
    }

    public String getContrattoLocazioneCodice() {
        return contrattoLocazioneCodice;
    }

    public void setContrattoLocazioneCodice(String contrattoLocazioneCodice) {
        this.contrattoLocazioneCodice = contrattoLocazioneCodice;
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
