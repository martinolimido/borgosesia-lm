package it.borgosesiaspa.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_idPianoCanone", columnList = "idPianoCanone"),
        @Index(name = "idx_idContratto", columnList = "idContratto")
})
public class Canone extends BaseEntity {
    /*- idContratto
    - idPianoCanone
    - periodoDa
    - periodoA
    - dataScadenza
    - importo
    - importoIncassato
    tipo (NORMALE, RIDOTTO, AGEVOLATO)
    - stato (EMESSO, PARZIALMENTE_INCASSATO, INCASSATO, INSOLUTO) */

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
    private ContrattoLocazione contrattoLocazione;

    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }
    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
    }
    public PianoCanone getPianoCanone() {
        return pianoCanone;
    }
    public void setPianoCanone(PianoCanone pianoCanone) {
        this.pianoCanone = pianoCanone;
    }
    public String getPeriodoDa() {
        return periodoDa;
    }
    public void setPeriodoDa(String periodoDa) {
        this.periodoDa = periodoDa;
    }
    public String getPeriodoA() {
        return periodoA;
    }
    public void setPeriodoA(String periodoA) {
        this.periodoA = periodoA;
    }
    public String getDataScadenza() {
        return dataScadenza;
    }
    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }
    public BigDecimal getImporto() {
        return importo;
    }
    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    public BigDecimal getImportoIncassato() {
        return importoIncassato;
    }
    public void setImportoIncassato(BigDecimal importoIncassato) {
        this.importoIncassato = importoIncassato;
    }
    public it.borgosesiaspa.model.enums.TipoCanone getTipo() {
        return tipo;
    }
    public void setTipo(it.borgosesiaspa.model.enums.TipoCanone tipo) {
        this.tipo = tipo;
    }
    public it.borgosesiaspa.model.enums.CanoneStato getStato() {
        return stato;
    }
    public void setStato(it.borgosesiaspa.model.enums.CanoneStato stato) {
        this.stato = stato;
    }
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_piano_canone", nullable = true)
    private PianoCanone pianoCanone;
    @Column
    private String periodoDa;
    @Column
    private String periodoA;
    @Column
    private String dataScadenza;
    @Column(precision = 10, scale = 2)
    private BigDecimal importo;
    @Column(precision = 10, scale = 2)
    private BigDecimal importoIncassato;
    @Column
    private it.borgosesiaspa.model.enums.TipoCanone tipo;
    @Column
    private it.borgosesiaspa.model.enums.CanoneStato stato;
}
