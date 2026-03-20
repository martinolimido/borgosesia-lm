package it.borgosesiaspa.dto.edit;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SpesaEditDto {
    private Long id;
    private Long contrattoLocazioneId;
    private LocalDate dataSpesa;
    private BigDecimal importo;
    private String riferimento;
    private String note;

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

    public LocalDate getDataSpesa() {
        return dataSpesa;
    }

    public void setDataSpesa(LocalDate dataSpesa) {
        this.dataSpesa = dataSpesa;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public String getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(String riferimento) {
        this.riferimento = riferimento;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
