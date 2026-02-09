package it.borgosesiaspa.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import it.borgosesiaspa.shared.util.BaseEntity;

@Entity
@Table(indexes = {
    @Index(name = "idx_idContratto", columnList = "idContratto"),
    @Index(name = "idx_tipoEvento", columnList = "tipoEvento"),
    @Index(name = "idx_riferimentoTipo", columnList = "riferimentoTipo")
})
public class EventoContratto extends BaseEntity {
  /*
   * 6️⃣ EventoContratto (la tua polizza vita)
   * 
   * 👉 Qui finisce ogni discussione futura.
   * EventoContratto
   * - id
   * - idContratto
   * - tipoEvento (
   * CREAZIONE,
   * ATTIVAZIONE,
   * GENERAZIONE_CANONE,
   * INCASSO,
   * MOROSITA_APERTA,
   * MOROSITA_CHIUSA,
   * VARIAZIONE_CANONE,
   * CESSAZIONE
   * )
   * - dataEvento
   * - riferimentoTipo (CANONE, INCASSO, MOROSITA, PIANO)
   * - riferimentoId
   * - payloadJson
   * - note
   * - createdBy
   * 
   * Payload JSON
   * 
   * Dentro ci metti:
   * • snapshot valori
   * • differenze
   * • contesto tecnico
   * 
   * Tra 2 anni, quando qualcuno chiederà
   * “perché questo canone è così?”
   * → tu apri EventoContratto e chiudi il ticket.
   * 
   */
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "id_contratto_locazione", nullable = true)
  private ContrattoLocazione contrattoLocazione;
  @Column
  private it.borgosesiaspa.model.enums.EventoTipo tipoEvento;
  @Column
  private LocalDate dataEvento;
  @Column
  private it.borgosesiaspa.model.enums.EventoRiferimento riferimentoTipo;
  @Column
  private Long riferimentoId;
  @Column(columnDefinition = "TEXT")
  private String payloadJson;
  @Column(columnDefinition = "TEXT")
  private String note;
  @Column
  private String createdBy;
  public ContrattoLocazione getContrattoLocazione() {
    return contrattoLocazione;
  }
  public void setContrattoLocazione(ContrattoLocazione contrattoLocazione) {
    this.contrattoLocazione = contrattoLocazione;
  }
  public it.borgosesiaspa.model.enums.EventoTipo getTipoEvento() {
    return tipoEvento;
  }
  public void setTipoEvento(it.borgosesiaspa.model.enums.EventoTipo tipoEvento) {
    this.tipoEvento = tipoEvento;
  }
  public LocalDate getDataEvento() {
    return dataEvento;
  }
  public void setDataEvento(LocalDate dataEvento) {
    this.dataEvento = dataEvento;
  }
  public it.borgosesiaspa.model.enums.EventoRiferimento getRiferimentoTipo() {
    return riferimentoTipo;
  }
  public void setRiferimentoTipo(it.borgosesiaspa.model.enums.EventoRiferimento riferimentoTipo) {
    this.riferimentoTipo = riferimentoTipo;
  }
  public Long getRiferimentoId() {
    return riferimentoId;
  }
  public void setRiferimentoId(Long riferimentoId) {
    this.riferimentoId = riferimentoId;
  }
  public String getPayloadJson() {
    return payloadJson;
  }
  public void setPayloadJson(String payloadJson) {
    this.payloadJson = payloadJson;
  }
  public String getNote() {
    return note;
  }
  public void setNote(String note) {
    this.note = note;
  }
  public String getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }
}
