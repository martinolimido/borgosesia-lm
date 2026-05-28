package it.borgosesiaspa.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.borgosesiaspa.dto.edit.AnomaliaContrattoStatoEditDto;
import it.borgosesiaspa.dto.read.AnomaliaContrattoReadDto;
import it.borgosesiaspa.model.AnomaliaContratto;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;
import it.borgosesiaspa.model.enums.StatoAnomalia;
import it.borgosesiaspa.repository.AnomaliaContrattoRepository;

/**
 * Operazioni di consultazione e gestione operativa delle anomalie rilevate.
 * Le anomalie vengono create dal task giornaliero — questo service espone
 * solo lettura e cambi di stato (IGNORA/RIAPRI).
 */
@Service
@Transactional
public class AnomaliaContrattoService {

    private final AnomaliaContrattoRepository anomaliaRepository;

    public AnomaliaContrattoService(AnomaliaContrattoRepository anomaliaRepository) {
        this.anomaliaRepository = anomaliaRepository;
    }

    @Transactional(readOnly = true)
    public Page<AnomaliaContrattoReadDto> searchAnomalie(
            Pageable pageable,
            Long contrattoLocazioneId,
            StatoAnomalia stato,
            SeveritaAnomalia severita,
            CodiceAnomalia codice) {
        return anomaliaRepository
                .searchAnomalie(pageable, contrattoLocazioneId, stato, severita, codice)
                .map(AnomaliaContrattoReadDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public AnomaliaContrattoReadDto getAnomalia(Long id) {
        return anomaliaRepository.findById(id)
                .map(AnomaliaContrattoReadDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Anomalia non trovata con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AnomaliaContrattoReadDto> getAnomalieContratto(Long contrattoLocazioneId, StatoAnomalia stato) {
        List<AnomaliaContratto> entities = (stato != null)
                ? anomaliaRepository.findByContrattoLocazioneIdAndStato(contrattoLocazioneId, stato)
                : anomaliaRepository.findByContrattoLocazioneId(contrattoLocazioneId);
        return entities.stream().map(AnomaliaContrattoReadDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public Map<SeveritaAnomalia, Long> conteggioApertePerSeverita() {
        Map<SeveritaAnomalia, Long> mappa = new HashMap<>();
        for (SeveritaAnomalia s : SeveritaAnomalia.values()) {
            mappa.put(s, anomaliaRepository.countByStatoAndSeverita(StatoAnomalia.APERTA, s));
        }
        return mappa;
    }

    public AnomaliaContrattoReadDto ignora(Long id, AnomaliaContrattoStatoEditDto dto) {
        AnomaliaContratto a = anomaliaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomalia non trovata con id: " + id));
        a.setStato(StatoAnomalia.IGNORATA);
        a.setDataRisoluzione(LocalDateTime.now());
        if (dto != null && dto.getNoteOperatore() != null) {
            a.setNoteOperatore(dto.getNoteOperatore());
        }
        anomaliaRepository.save(a);
        return AnomaliaContrattoReadDto.fromEntity(a);
    }

    public AnomaliaContrattoReadDto riapri(Long id, AnomaliaContrattoStatoEditDto dto) {
        AnomaliaContratto a = anomaliaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomalia non trovata con id: " + id));
        a.setStato(StatoAnomalia.APERTA);
        a.setDataRisoluzione(null);
        if (dto != null && dto.getNoteOperatore() != null) {
            a.setNoteOperatore(dto.getNoteOperatore());
        }
        anomaliaRepository.save(a);
        return AnomaliaContrattoReadDto.fromEntity(a);
    }
}
