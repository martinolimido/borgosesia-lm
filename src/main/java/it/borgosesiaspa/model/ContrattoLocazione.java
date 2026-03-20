package it.borgosesiaspa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.DecorrenzaISTAT;
import it.borgosesiaspa.model.enums.Periodicita;
import it.borgosesiaspa.model.enums.TipologiaRinnovo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import it.borgosesiaspa.shared.util.BaseEntity;

@Entity
@Table(indexes = {
        @Index(name = "idx_idImmobile", columnList = "idImmobile"),
        @Index(name = "idx_idConduttore", columnList = "idConduttore")
})
public class ContrattoLocazione extends BaseEntity {
    @Column
    private String descrizione;

    // Fanno riferimenti ad entità esterne (Immobile, Unità, Conduttore) che non
    // gestiamo direttamente, quindi manteniamo solo gli ID
    @Column
    private Integer idImmobile;
    @Column
    private Integer idConduttore;

    @Column
    private LocalDate dataFine;
    @Column
    private LocalDate dataPrimaScadenza;
    @Column
    private Integer durataMesi;
    @Column(precision = 10, scale = 2)
    private BigDecimal canoneBase;
    @Column(precision = 10, scale = 2)
    private BigDecimal speseAccessorieRiaddebitabili;

    public BigDecimal getSpeseAccessorieRiaddebitabili() {
        return speseAccessorieRiaddebitabili;
    }

    public void setSpeseAccessorieRiaddebitabili(BigDecimal speseAccessorieRiaddebitabili) {
        this.speseAccessorieRiaddebitabili = speseAccessorieRiaddebitabili;
    }

    private Boolean speseAccessorieCaricoConduttore;

    public Boolean getSpeseAccessorieCaricoConduttore() {
        return speseAccessorieCaricoConduttore;
    }

    public void setSpeseAccessorieCaricoConduttore(Boolean speseAccessorieCaricoConduttore) {
        this.speseAccessorieCaricoConduttore = speseAccessorieCaricoConduttore;
    }

    @Column
    private Periodicita periodicita;
    @Column
    private Boolean rivalutazioneIstat;
    @Column
    private LocalDate dataProssimaRivalutazioneIstat;

    public LocalDate getDataProssimaRivalutazioneIstat() {
        return dataProssimaRivalutazioneIstat;
    }

    public void setDataProssimaRivalutazioneIstat(LocalDate dataProssimaRivalutazioneIstat) {
        this.dataProssimaRivalutazioneIstat = dataProssimaRivalutazioneIstat;
    }

    public Boolean getAzioneLegaleInCorso() {
        return azioneLegaleInCorso;
    }

    public void setAzioneLegaleInCorso(Boolean azioneLegaleInCorso) {
        this.azioneLegaleInCorso = azioneLegaleInCorso;
    }

    @Column
    private Boolean azioneLegaleInCorso;
    @Column
    private Boolean canoneVariabile;

    public Boolean getCanoneVariabile() {
        return canoneVariabile;
    }

    public void setCanoneVariabile(Boolean canoneVariabile) {
        this.canoneVariabile = canoneVariabile;
    }

    @Column
    private DecorrenzaISTAT decorrenzaIstat;

    public DecorrenzaISTAT getDecorrenzaIstat() {
        return decorrenzaIstat;
    }

    public void setDecorrenzaIstat(DecorrenzaISTAT decorrenzaISTAT) {
        this.decorrenzaIstat = decorrenzaISTAT;
    }

    @Column(precision = 5, scale = 2)
    private BigDecimal percentualeIstat;
    @Column(precision = 10, scale = 2)
    private BigDecimal depositoCauzionale;
    @Column
    private ContrattoStato stato;
    @Column
    private String tipologia;
    @Column
    private TipologiaRinnovo tipologiaRinnovo;
    @Column
    private Integer mesiPreavviso;
    @Column
    private LocalDate dataCessazione;
    @Column(columnDefinition = "TEXT")
    private String note;

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public TipologiaRinnovo getTipologiaRinnovo() {
        return tipologiaRinnovo;
    }

    public void setTipologiaRinnovo(TipologiaRinnovo tipologiaRinnovo) {
        this.tipologiaRinnovo = tipologiaRinnovo;
    }

    public Integer getMesiPreavviso() {
        return mesiPreavviso;
    }

    public void setMesiPreavviso(Integer mesiPreavviso) {
        this.mesiPreavviso = mesiPreavviso;
    }

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

    public List<ContrattoLocazioneUnita> getUnita() {
        return unita;
    }

    public void setUnita(List<ContrattoLocazioneUnita> unita) {
        this.unita.clear();
        if (unita == null) {
            return;
        }
        for (ContrattoLocazioneUnita contrattoUnita : unita) {
            addUnita(contrattoUnita);
        }
    }

    public void addUnita(ContrattoLocazioneUnita contrattoUnita) {
        contrattoUnita.setContrattoLocazione(this);
        unita.add(contrattoUnita);
    }

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

    @OneToMany(mappedBy = "contrattoLocazione", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ContrattoLocazioneUnita> unita = new ArrayList<>();

}
