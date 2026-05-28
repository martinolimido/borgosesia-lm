package it.borgosesiaspa.dto.edit;

import java.math.BigDecimal;
import java.time.LocalDate;

import it.borgosesiaspa.model.enums.Periodicita;
import it.borgosesiaspa.model.enums.StatoPianoCanone;
import it.borgosesiaspa.model.enums.TipoCanone;

public class PianoCanoneEditDto {
    private Long id;
    private Long contrattoLocazioneId;
    private LocalDate dataInizioValidita;
    private LocalDate dataFineValidita;
    private LocalDate dataAnnullamento;
    private BigDecimal importo;
    private Periodicita periodicita;
    private Integer giornoScadenza;
    private TipoCanone tipo;
    private String note;
    /**
     * Se true, tutti i canoni generati con dataScadenza anteriore al 1° del mese
     * corrente vengono marcati INCASSATO con un Incasso alla data di scadenza
     * (simulazione incasso puntuale). Utile al primo caricamento di un contratto
     * già in corso.
     */
    private Boolean incassaPregressi;

    public Boolean getIncassaPregressi() {
        return incassaPregressi;
    }

    public void setIncassaPregressi(Boolean incassaPregressi) {
        this.incassaPregressi = incassaPregressi;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataAnnullamento() {
        return dataAnnullamento;
    }

    public void setDataAnnullamento(LocalDate dataAnnullamento) {
        this.dataAnnullamento = dataAnnullamento;
    }

    public StatoPianoCanone getStato() {
        var oggi = LocalDate.now();
        var dataInizio = this.dataInizioValidita;
        var dataFine = this.dataFineValidita;
        if (dataAnnullamento != null)
            return StatoPianoCanone.ANNULLATO;
        if (oggi.isBefore(dataInizio))
            return StatoPianoCanone.PROGRAMMATO;
        if (dataFine != null && oggi.isAfter(dataFine))
            return StatoPianoCanone.TERMINATO;
        return StatoPianoCanone.ATTIVO;
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

    public LocalDate getDataInizioValidita() {
        return dataInizioValidita;
    }

    public void setDataInizioValidita(LocalDate dataInizioValidita) {
        this.dataInizioValidita = dataInizioValidita;
    }

    public LocalDate getDataFineValidita() {
        return dataFineValidita;
    }

    public void setDataFineValidita(LocalDate dataFineValidita) {
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
