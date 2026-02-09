package it.borgosesiaspa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    @Column(nullable = false)
    private LocalDateTime dataModifica;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long idUtenteCreatore;

    public Long getIdUtenteCreatore() {
        return idUtenteCreatore;
    }

    public void setIdUtenteCreatore(Long idUtenteCreatore) {
        this.idUtenteCreatore = idUtenteCreatore;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime createdAt) {
        this.dataCreazione = createdAt;
    }

    public LocalDateTime getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(LocalDateTime updatedAt) {
        this.dataModifica = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        setDataCreazione(now);
        setDataModifica(now);
    }

    @PreUpdate
    public void preUpdate() {
        var now = LocalDateTime.now();
        setDataModifica(now);
    }
}
