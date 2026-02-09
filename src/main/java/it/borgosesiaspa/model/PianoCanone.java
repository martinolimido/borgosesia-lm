package it.borgosesiaspa.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import it.borgosesiaspa.model.enums.Periodicita;
import it.borgosesiaspa.model.enums.TipoCanone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import it.borgosesiaspa.shared.util.BaseEntity;

@Entity
@Table(indexes = {
        @Index(name = "idx_idContratto", columnList = "idContratto")
})
public class PianoCanone extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
    private ContrattoLocazione contrattoLocazione;
    @Column
    private String dataInizioValidita;
    @Column
    private String dataFineValidita;
    @Column(precision = 10, scale = 2)
    private BigDecimal importo;
    @Column
    private Periodicita periodicita;
    @Column
    private Integer giornoScadenza;
    @Column
    private TipoCanone tipo;
    @Column(columnDefinition = "TEXT")
    private String note;
    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }
    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
    }
    public String getDataInizioValidita() {
        return dataInizioValidita;
    }
    public void setDataInizioValidita(String dataInizioValidita) {
        this.dataInizioValidita = dataInizioValidita;
    }
    public String getDataFineValidita() {
        return dataFineValidita;
    }
    public void setDataFineValidita(String dataFineValidita) {
        this.dataFineValidita = dataFineValidita;
    }
    public BigDecimal getImporto() {
        return importo;
    }
    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    public Periodicita getPeriodicita() {
        return periodicita;
    }
    public void setPeriodicita(Periodicita periodicita) {
        this.periodicita = periodicita;
    }
    public Integer getGiornoScadenza() {
        return giornoScadenza;
    }
    public void setGiornoScadenza(Integer giornoScadenza) {
        this.giornoScadenza = giornoScadenza;
    }
    public TipoCanone getTipo() {
        return tipo;
    }
    public void setTipo(TipoCanone tipo) {
        this.tipo = tipo;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
