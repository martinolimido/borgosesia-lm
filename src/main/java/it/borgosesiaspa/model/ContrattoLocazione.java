package it.borgosesiaspa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.Periodicita;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;

@Entity
@Table(indexes = {
        @Index(name = "idx_idImmobile", columnList = "idImmobile"),
        @Index(name = "idx_idConduttore", columnList = "idConduttore"),
        @Index(name = "idx_idUnita", columnList = "idUnita")
})
public class ContrattoLocazione extends BaseEntity {
    @Column
    private String descrizione;

    // Fanno riferimenti ad entità esterne (Immobile, Unità, Conduttore) che non
    // gestiamo direttamente, quindi manteniamo solo gli ID
    @Column
    private Integer idImmobile;
    @Column
    private Integer idUnita;
    @Column
    private Integer idConduttore;

    @Column
    private String codiceContratto;
    @Column
    private LocalDate dataInizio;
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getIdImmobile() {
        return idImmobile;
    }

    public void setIdImmobile(Integer idImmobile) {
        this.idImmobile = idImmobile;
    }

    public Integer getIdUnita() {
        return idUnita;
    }

    public void setIdUnita(Integer idUnita) {
        this.idUnita = idUnita;
    }

    public Integer getIdConduttore() {
        return idConduttore;
    }

    public void setIdConduttore(Integer idConduttore) {
        this.idConduttore = idConduttore;
    }

    public String getCodiceContratto() {
        return codiceContratto;
    }

    public void setCodiceContratto(String codiceContratto) {
        this.codiceContratto = codiceContratto;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public LocalDate getDataPrimaScadenza() {
        return dataPrimaScadenza;
    }

    public void setDataPrimaScadenza(LocalDate dataPrimaScadenza) {
        this.dataPrimaScadenza = dataPrimaScadenza;
    }

    public Integer getDurataMesi() {
        return durataMesi;
    }

    public void setDurataMesi(Integer durataMesi) {
        this.durataMesi = durataMesi;
    }

    public BigDecimal getCanoneBase() {
        return canoneBase;
    }

    public void setCanoneBase(BigDecimal canoneBase) {
        this.canoneBase = canoneBase;
    }

    public Periodicita getPeriodicita() {
        return periodicita;
    }

    public void setPeriodicita(Periodicita periodicita) {
        this.periodicita = periodicita;
    }

    public Boolean getRivalutazioneIstat() {
        return rivalutazioneIstat;
    }

    public void setRivalutazioneIstat(Boolean rivalutazioneIstat) {
        this.rivalutazioneIstat = rivalutazioneIstat;
    }

    public BigDecimal getPercentualeIstat() {
        return percentualeIstat;
    }

    public void setPercentualeIstat(BigDecimal percentualeIstat) {
        this.percentualeIstat = percentualeIstat;
    }

    public BigDecimal getDepositoCauzionale() {
        return depositoCauzionale;
    }

    public void setDepositoCauzionale(BigDecimal depositoCauzionale) {
        this.depositoCauzionale = depositoCauzionale;
    }

    public ContrattoStato getStato() {
        return stato;
    }

    public void setStato(ContrattoStato stato) {
        this.stato = stato;
    }

    public LocalDate getDataCessazione() {
        return dataCessazione;
    }

    public void setDataCessazione(LocalDate dataCessazione) {
        this.dataCessazione = dataCessazione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Canone> getCanoni() {
        return canoni;
    }

    public void setCanoni(List<Canone> canoni) {
        this.canoni = canoni;
    }

    public List<Incasso> getIncassi() {
        return incassi;
    }

    public void setIncassi(List<Incasso> incassi) {
        this.incassi = incassi;
    }

    public List<Morosita> getMorosita() {
        return morosita;
    }

    public void setMorosita(List<Morosita> morosita) {
        this.morosita = morosita;
    }

    public List<PianoCanone> getPianiCanone() {
        return pianiCanone;
    }

    public void setPianiCanone(List<PianoCanone> pianiCanone) {
        this.pianiCanone = pianiCanone;
    }

    public List<EventoContratto> getEventiContratto() {
        return eventiContratto;
    }

    public void setEventiContratto(List<EventoContratto> eventiContratto) {
        this.eventiContratto = eventiContratto;
    }

    @Column
    private LocalDate dataFine;
    @Column
    private LocalDate dataPrimaScadenza;
    @Column
    private Integer durataMesi;
    @Column(precision = 10, scale = 2)
    private BigDecimal canoneBase;
    @Column
    private Periodicita periodicita;
    @Column
    private Boolean rivalutazioneIstat;
    @Column(precision = 5, scale = 2)
    private BigDecimal percentualeIstat;
    @Column(precision = 10, scale = 2)
    private BigDecimal depositoCauzionale;
    @Column
    private ContrattoStato stato;
    @Column
    private LocalDate dataCessazione;
    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Canone> canoni;

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Incasso> incassi;

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Morosita> morosita;

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PianoCanone> pianiCanone;

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EventoContratto> eventiContratto;

}
