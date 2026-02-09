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
import it.borgosesiaspa.shared.util.BaseEntity;

@Entity
@Table(indexes = {
        @Index(name = "idx_idContratto", columnList = "idContratto"),
        @Index(name = "idx_idCanone", columnList = "idCanone")
})
public class Morosita extends BaseEntity {
    /*
     * 👉 La morosità non è solo un calcolo, è uno stato gestionale.
     * Perché persisterla
     * • reporting
     * • workflow (solleciti, legali)
     * • storico decisionale
     * Morosita
     * - idContratto
     * - idCanone
     * - dataInizio
     * - giorniRitardo
     * - importoResiduo
     * - stato (APERTA, IN_SOLLECITO, RIENTRATA, CHIUSA)
     * - livello (LIEVE, MEDIA, GRAVE)
     * - note
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
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
    public LocalDate getDataInizio() {
        return dataInizio;
    }
    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }
    public Integer getGiorniRitardo() {
        return giorniRitardo;
    }
    public void setGiorniRitardo(Integer giorniRitardo) {
        this.giorniRitardo = giorniRitardo;
    }
    public BigDecimal getImportoResiduo() {
        return importoResiduo;
    }
    public void setImportoResiduo(BigDecimal importoResiduo) {
        this.importoResiduo = importoResiduo;
    }
    public it.borgosesiaspa.model.enums.StatoMorosita getStato() {
        return stato;
    }
    public void setStato(it.borgosesiaspa.model.enums.StatoMorosita stato) {
        this.stato = stato;
    }
    public it.borgosesiaspa.model.enums.LivelloMorosita getLivello() {
        return livello;
    }
    public void setLivello(it.borgosesiaspa.model.enums.LivelloMorosita livello) {
        this.livello = livello;
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
    private LocalDate dataInizio;
    @Column
    private Integer giorniRitardo;
    @Column(precision = 10, scale = 2)
    private BigDecimal importoResiduo;
    @Column
    private it.borgosesiaspa.model.enums.StatoMorosita stato;
    @Column
    private it.borgosesiaspa.model.enums.LivelloMorosita livello;
    @Column
    private String riferimento;
    @Column(columnDefinition = "TEXT")
    private String note;
}
