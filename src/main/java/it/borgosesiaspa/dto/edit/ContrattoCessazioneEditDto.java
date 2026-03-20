package it.borgosesiaspa.dto.edit;

import java.time.LocalDate;

public class ContrattoCessazioneEditDto {
    private LocalDate dataCessazione;

    public LocalDate getDataCessazione() {
        return dataCessazione;
    }

    public void setDataCessazione(LocalDate dataCessazione) {
        this.dataCessazione = dataCessazione;
    }
}
