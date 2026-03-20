package it.borgosesiaspa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import it.borgosesiaspa.shared.util.BaseEntity;

@Entity
@Table(indexes = {
        @Index(name = "idx_spesa_idContratto", columnList = "idContrattoLocazione"),
        @Index(name = "idx_spesa_dataSpesa", columnList = "dataSpesa")
})
public class Spesa extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
    private ContrattoLocazione contrattoLocazione;

    @Column(nullable = false)
    private LocalDate dataSpesa;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal importo;

    @Column
    private String riferimento;

    @Column(columnDefinition = "TEXT")
    private String note;

    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }

    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
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
