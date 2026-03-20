package it.borgosesiaspa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import it.borgosesiaspa.shared.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_clu_idContratto", columnList = "idContrattoLocazione"),
        @Index(name = "idx_clu_idUnita", columnList = "idUnita")
})
public class ContrattoLocazioneUnita extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "id_contratto_locazione", nullable = false)
    private ContrattoLocazione contrattoLocazione;

    @Column(nullable = false)
    private Integer idUnita;

    public ContrattoLocazione getContrattoLocazione() {
        return contrattoLocazione;
    }

    public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
        this.contrattoLocazione = contrattoLocazione;
    }

    public Integer getIdUnita() {
        return idUnita;
    }

    public void setIdUnita(Integer idUnita) {
        this.idUnita = idUnita;
    }
}
