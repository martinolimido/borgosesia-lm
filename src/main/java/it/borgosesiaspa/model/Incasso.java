package it.borgosesiaspa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(indexes = {
        @Index(name = "idx_idContratto", columnList = "idContratto"),
        @Index(name = "idx_idCanone", columnList = "idCanone")
})
public class Incasso extends BaseEntity {
    /*
     * 👉 Gli incassi non mentono mai.
     * Regole sane
     * • può essere:
     * • associato a un canone
     * • libero (anticipo, saldo, deposito)
     * • mai cancellato
     * • se errore → storno / evento
     * 
     * - idContratto
     * - idCanone (nullable)
     * - dataIncasso
     * - importo
     * - metodo (BONIFICO, CONTANTI, ALTRO)
     * - riferimento
     * - note
     */
    

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = true)
    private ContrattoLocazione contrattoLocazione;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_canone", nullable = true)
    private Canone canone;

    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }
    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
    }
    public Canone getCanone() {
        return canone;
    }
    public void setCanone(Canone canone) {
        this.canone = canone;
    }
    public LocalDate getDataIncasso() {
        return dataIncasso;
    }
    public void setDataIncasso(LocalDate dataIncasso) {
        this.dataIncasso = dataIncasso;
    }
    public BigDecimal getImporto() {
        return importo;
    }
    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }
    public it.borgosesiaspa.model.enums.MetodoIncasso getMetodo() {
        return metodo;
    }
    public void setMetodo(it.borgosesiaspa.model.enums.MetodoIncasso metodo) {
        this.metodo = metodo;
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
    @Column
    private LocalDate dataIncasso;
    @Column(precision = 10, scale = 2)
    private BigDecimal importo;
    @Column
    private it.borgosesiaspa.model.enums.MetodoIncasso metodo;
    @Column
    private String riferimento;
    @Column(columnDefinition = "TEXT")
    private String note;
}
