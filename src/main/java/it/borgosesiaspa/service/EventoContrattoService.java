package it.borgosesiaspa.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.borgosesiaspa.dto.edit.EventoContrattoEditDto;
import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.EventoContratto;
import it.borgosesiaspa.model.Incasso;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.EventoRiferimento;
import it.borgosesiaspa.model.enums.EventoTipo;
import it.borgosesiaspa.repository.ContrattoLocazioneRepository;
import it.borgosesiaspa.repository.EventoContrattoRepository;

@Service
@Transactional
public class EventoContrattoService {

    private final EventoContrattoRepository eventoContrattoRepository;
    private final ContrattoLocazioneRepository contrattoLocazioneRepository;

    public EventoContrattoService(
            EventoContrattoRepository eventoContrattoRepository,
            ContrattoLocazioneRepository contrattoLocazioneRepository) {
        this.contrattoLocazioneRepository = contrattoLocazioneRepository;
        this.eventoContrattoRepository = eventoContrattoRepository;
    }

    private ContrattoLocazione findContrattoLocazione(Long id) {
        return contrattoLocazioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ContrattoLocazione non trovato con id: " + id));
    }

    public void createEventoContratto(EventoContrattoEditDto dto) {
        EventoContratto entity = new EventoContratto();
        applyEventoContratto(entity, dto);
        eventoContrattoRepository.save(entity);
    }

    public void registraIncassoCanone(Canone canone, Incasso incasso, String username) {
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(canone.getContrattoLocazione().getId());
        eventoDto.setTipoEvento(EventoTipo.INCASSO);
        eventoDto.setDataEvento(incasso.getDataIncasso());
        eventoDto.setRiferimentoTipo(EventoRiferimento.CANONE);
        eventoDto.setRiferimentoId(canone.getId());
        eventoDto.setPayloadJson("{\"importo\": " + incasso.getImporto() + "}");
        eventoDto.setNote("Incasso canone con importo " + incasso.getImporto());
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    public void apriMorositaCanone(Canone canone, Morosita morosita, String username) {
        BigDecimal diff = canone.getImporto().subtract(canone.getImportoIncassato());
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(canone.getContrattoLocazione().getId());
        eventoDto.setTipoEvento(EventoTipo.MOROSITA_APERTA);
        eventoDto.setDataEvento(LocalDate.now());
        eventoDto.setRiferimentoTipo(EventoRiferimento.CANONE);
        eventoDto.setRiferimentoId(canone.getId());
        eventoDto.setPayloadJson("{\"importo\": " + diff + "}");
        eventoDto.setNote("Apertura morosità canone con importo " + diff);
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    public void chiudiMorosita(Morosita morosita, String username) {
        BigDecimal importoResiduo = morosita.getImportoResiduo() != null ? morosita.getImportoResiduo() : BigDecimal.ZERO;
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(morosita.getContrattoLocazione().getId());
        eventoDto.setTipoEvento(EventoTipo.MOROSITA_CHIUSA);
        eventoDto.setDataEvento(LocalDate.now());
        eventoDto.setRiferimentoTipo(EventoRiferimento.MOROSITA);
        eventoDto.setRiferimentoId(morosita.getId());
        eventoDto.setPayloadJson("{\"importoResiduo\": " + importoResiduo + "}");
        eventoDto.setNote("Chiusura morosità con importo residuo " + importoResiduo);
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    public void cancelPianoCanone(PianoCanone entity, String username) {
        cancelPianoCanone(entity, LocalDate.now(), username);
    }

    public void cancelPianoCanone(PianoCanone entity, LocalDate dataEvento, String username) {
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(entity.getContrattoLocazione().getId());
        eventoDto.setTipoEvento(EventoTipo.VARIAZIONE_CANONE);
        eventoDto.setDataEvento(dataEvento);
        eventoDto.setRiferimentoTipo(EventoRiferimento.PIANO);
        eventoDto.setRiferimentoId(entity.getId());
        eventoDto.setPayloadJson("{\"importo\": " + entity.getImporto() + "}");
        eventoDto.setNote("Annullamento piano canone e canoni emessi con importo " + entity.getImporto());
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    public void cessaContratto(ContrattoLocazione entity, int pianiAnnullati, String username) {
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(entity.getId());
        eventoDto.setTipoEvento(EventoTipo.CESSAZIONE);
        eventoDto.setDataEvento(entity.getDataCessazione());
        eventoDto.setRiferimentoTipo(EventoRiferimento.CONTRATTO);
        eventoDto.setRiferimentoId(entity.getId());
        eventoDto.setPayloadJson("{\"dataCessazione\": \"" + entity.getDataCessazione() + "\", \"pianiAnnullati\": " + pianiAnnullati + "}");
        eventoDto.setNote("Cessazione contratto con annullamento di " + pianiAnnullati + " piani canone attivi");
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    public void createPianoCanone(PianoCanone entity, String username) {
        EventoContrattoEditDto eventoDto = new EventoContrattoEditDto();
        eventoDto.setContrattoLocazioneId(entity.getContrattoLocazione().getId());
        eventoDto.setTipoEvento(EventoTipo.CREAZIONE);
        eventoDto.setDataEvento(LocalDate.now());
        eventoDto.setRiferimentoTipo(EventoRiferimento.PIANO);
        eventoDto.setRiferimentoId(entity.getId());
        eventoDto.setPayloadJson("{\"importo\": " + entity.getImporto() + "}");
        eventoDto.setNote("Creazione piano canone con importo " + entity.getImporto());
        eventoDto.setCreatedBy(username);
        createEventoContratto(eventoDto);
    }

    private void applyEventoContratto(EventoContratto entity, EventoContrattoEditDto dto) {
        entity.setContrattoLocazione(dto.getContrattoLocazioneId() != null
                ? findContrattoLocazione(dto.getContrattoLocazioneId())
                : null);
        entity.setTipoEvento(dto.getTipoEvento());
        entity.setDataEvento(dto.getDataEvento());
        entity.setRiferimentoTipo(dto.getRiferimentoTipo());
        entity.setRiferimentoId(dto.getRiferimentoId());
        entity.setPayloadJson(dto.getPayloadJson());
        entity.setNote(dto.getNote());
        entity.setCreatedBy(dto.getCreatedBy());
    }
}
