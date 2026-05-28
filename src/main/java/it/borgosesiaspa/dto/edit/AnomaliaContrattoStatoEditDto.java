package it.borgosesiaspa.dto.edit;

/**
 * DTO usato per aggiornare lo stato operativo di un'{@link
 * it.borgosesiaspa.model.AnomaliaContratto}, tipicamente per portarla a
 * IGNORATA o riaprirla.
 */
public class AnomaliaContrattoStatoEditDto {

    private String noteOperatore;

    public AnomaliaContrattoStatoEditDto() {
    }

    public String getNoteOperatore() {
        return noteOperatore;
    }

    public void setNoteOperatore(String noteOperatore) {
        this.noteOperatore = noteOperatore;
    }
}
