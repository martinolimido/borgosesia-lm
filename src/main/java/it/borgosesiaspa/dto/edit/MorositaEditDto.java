package it.borgosesiaspa.dto.edit;

import java.math.BigDecimal;
import java.time.LocalDate;

import it.borgosesiaspa.model.enums.LivelloMorosita;
import it.borgosesiaspa.model.enums.StatoMorosita;

public class MorositaEditDto {
    private Long id;
    private Long contrattoLocazioneId;
    private Long canoneId;
    private LocalDate dataInizio;
    private Integer giorniRitardo;
    private LocalDate dataPrevisioneIncasso;
    private BigDecimal importoResiduo;
    private StatoMorosita stato;
    private LivelloMorosita livello;
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

    public Long getCanoneId() {
        return canoneId;
    }

    public void setCanoneId(Long canoneId) {
        this.canoneId = canoneId;
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

    public LocalDate getDataPrevisioneIncasso() {
        return dataPrevisioneIncasso;
    }

    public void setDataPrevisioneIncasso(LocalDate dataPrevisioneIncasso) {
        this.dataPrevisioneIncasso = dataPrevisioneIncasso;
    }

    public BigDecimal getImportoResiduo() {
        return importoResiduo;
    }

    public void setImportoResiduo(BigDecimal importoResiduo) {
        this.importoResiduo = importoResiduo;
    }

    public StatoMorosita getStato() {
        return stato;
    }

    public void setStato(StatoMorosita stato) {
        this.stato = stato;
    }

    public LivelloMorosita getLivello() {
        return livello;
    }

    public void setLivello(LivelloMorosita livello) {
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
}
