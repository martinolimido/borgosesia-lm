package it.borgosesiaspa.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import it.borgosesiaspa.dto.edit.CanoneEditDto;
import it.borgosesiaspa.dto.edit.CanoneListDto;
import it.borgosesiaspa.dto.edit.ContrattoLocazioneEditDto;
import it.borgosesiaspa.dto.edit.EventoContrattoEditDto;
import it.borgosesiaspa.dto.edit.IncassoEditDto;
import it.borgosesiaspa.dto.edit.MorositaEditDto;
import it.borgosesiaspa.dto.edit.PianoCanoneEditDto;
import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.EventoContratto;
import it.borgosesiaspa.model.Incasso;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.enums.StatoCanone;
import it.borgosesiaspa.model.enums.StatoPianoCanone;
import it.borgosesiaspa.repository.CanoneRepository;
import it.borgosesiaspa.repository.ContrattoLocazioneRepository;
import it.borgosesiaspa.repository.IncassoRepository;
import it.borgosesiaspa.repository.MorositaRepository;
import it.borgosesiaspa.repository.PianoCanoneRepository;

@Service
@Transactional
public class EntityEditingService {

    private final ContrattoLocazioneRepository contrattoLocazioneRepository;
    private final CanoneRepository canoneRepository;
    private final IncassoRepository incassoRepository;
    private final MorositaRepository morositaRepository;
    private final PianoCanoneRepository pianoCanoneRepository;
    // private final EventoContrattoRepository eventoContrattoRepository;
    private final EventoContrattoService eventoContrattoService;

    public EntityEditingService(
            ContrattoLocazioneRepository contrattoLocazioneRepository,
            CanoneRepository canoneRepository,
            IncassoRepository incassoRepository,
            MorositaRepository morositaRepository,
            PianoCanoneRepository pianoCanoneRepository,
            EventoContrattoService eventoContrattoService) {
        this.contrattoLocazioneRepository = contrattoLocazioneRepository;
        this.canoneRepository = canoneRepository;
        this.incassoRepository = incassoRepository;
        this.morositaRepository = morositaRepository;
        this.pianoCanoneRepository = pianoCanoneRepository;
        this.eventoContrattoService = eventoContrattoService;
    }

    @Transactional(readOnly = true)
    public Page<ContrattoLocazioneEditDto> listContrattiLocazione(Pageable pageable) {
        return contrattoLocazioneRepository.findAll(pageable).map(this::toDto);
    }

    public ContrattoLocazioneEditDto createContrattoLocazione(ContrattoLocazioneEditDto dto) {
        ContrattoLocazione entity = new ContrattoLocazione();
        applyContrattoLocazione(entity, dto);
        return toDto(contrattoLocazioneRepository.save(entity));
    }

    public void deleteContrattoLocazione(Long id) {
        ContrattoLocazione entity = findContrattoLocazione(id);
        contrattoLocazioneRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public ContrattoLocazioneEditDto getContrattoLocazione(Long id) {
        return toDto(findContrattoLocazione(id));
    }

    @Transactional(readOnly = true)
    public ContrattoLocazioneEditDto getContrattoLocazioneByCodice(String codice) {
        return toDto(findContrattoLocazioneByCodice(codice));
    }

    @Transactional(readOnly = true)
    public List<PianoCanoneEditDto> getContrattoLocazionePianiCanone(Long idContrattoLocazione) {
        try {
            List<PianoCanone> entities = findContrattoLocazionePianiCanone(idContrattoLocazione);
            return entities != null ? entities.stream().map(this::toDto).toList() : List.of();
        } catch (ResponseStatusException e) {
        }
        return List.of();
    }

    public ContrattoLocazioneEditDto updateContrattoLocazione(Long id, ContrattoLocazioneEditDto dto) {
        ContrattoLocazione entity = findContrattoLocazione(id);
        applyContrattoLocazione(entity, dto);
        return toDto(contrattoLocazioneRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<CanoneEditDto> listCanoni(Pageable pageable) {
        return canoneRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CanoneListDto> searchCanoni(Pageable pageable, Long contrattoLocazioneId,
            Long pianoCanoneId,
            LocalDate scadenzaDa,
            LocalDate scadenzaA,
            List<StatoCanone> statiCanone) {
        Page<Canone> entities = canoneRepository.searchCanoni(pageable, contrattoLocazioneId, pianoCanoneId, scadenzaDa, scadenzaA, statiCanone);
        return entities.map(this::toListDto);
    }

    public CanoneEditDto createCanone(CanoneEditDto dto) {
        Canone entity = new Canone();
        applyCanone(entity, dto);
        return toDto(canoneRepository.save(entity));
    }

    public void deleteCanone(Long id) {
        Canone entity = findCanone(id);
        canoneRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public CanoneEditDto getCanone(Long id) {
        return toDto(findCanone(id));
    }

    public CanoneEditDto updateCanone(Long id, CanoneEditDto dto) {
        Canone entity = findCanone(id);
        applyCanone(entity, dto);
        return toDto(canoneRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<IncassoEditDto> listIncassi(Pageable pageable) {
        return incassoRepository.findAll(pageable).map(this::toDto);
    }

    public IncassoEditDto registraIncassoCanone(Long canoneId, IncassoEditDto dto, String username) {
        if (dto.getCanoneId() != null && !dto.getCanoneId().equals(canoneId)) {
            throw badRequest("Il canoneId nel path e nel body devono essere uguali");
        }
        Canone canone = findCanone(canoneId);
        if (canone != null) {
            CanoneEditDto canoneDto = toDto(canone);
            BigDecimal incassatoFinoAdOra = canoneDto.getImportoIncassato() != null ? canoneDto.getImportoIncassato() : BigDecimal.ZERO;
            BigDecimal incassoAttuale = dto.getImporto() != null ? dto.getImporto() : BigDecimal.ZERO;
            canoneDto.setImportoIncassato(incassatoFinoAdOra.add(incassoAttuale));
            boolean incassoCompleto = canoneDto.getImportoIncassato().compareTo(canoneDto.getImporto()) >= 0;
            canoneDto.setStato(incassoCompleto ? StatoCanone.INCASSATO : StatoCanone.PARZIALMENTE_INCASSATO);
            applyCanone(canone, canoneDto);
            canoneRepository.save(canone);
            Incasso entity = new Incasso();
            applyIncasso(entity, dto);
            entity = incassoRepository.save(entity);
            eventoContrattoService.registraIncassoCanone(canone, entity, username);
            return toDto(entity);
        }
        throw notFound("Canone", canoneId);
    }

    public MorositaEditDto apriMorositaCanone(Long canoneId, MorositaEditDto dto, String username) {
        if (dto.getCanoneId() != null && !dto.getCanoneId().equals(canoneId)) {
            throw badRequest("Il canoneId nel path e nel body devono essere uguali");
        }
        Canone canone = findCanone(canoneId);
        if (canone != null) {
            CanoneEditDto canoneDto = toDto(canone);
            boolean incassoNullo = canoneDto.getImportoIncassato().compareTo(BigDecimal.ZERO) == 0;
            canoneDto.setStato(incassoNullo ? StatoCanone.INSOLUTO : StatoCanone.PARZIALMENTE_INCASSATO);
            applyCanone(canone, canoneDto);
            canoneRepository.save(canone);
            Morosita entity = new Morosita();
            dto.setImportoResiduo(canoneDto.getImporto().subtract(canoneDto.getImportoIncassato()));
            applyMorosita(entity, dto);
            entity = morositaRepository.save(entity);
            eventoContrattoService.apriMorositaCanone(canone, entity, username);
            return toDto(entity);
        }
        throw notFound("Canone", canoneId);
    }

    public IncassoEditDto createIncasso(IncassoEditDto dto) {
        Incasso entity = new Incasso();
        applyIncasso(entity, dto);
        return toDto(incassoRepository.save(entity));
    }

    public void deleteIncasso(Long id) {
        Incasso entity = findIncasso(id);
        incassoRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public IncassoEditDto getIncasso(Long id) {
        return toDto(findIncasso(id));
    }

    public IncassoEditDto updateIncasso(Long id, IncassoEditDto dto) {
        Incasso entity = findIncasso(id);
        applyIncasso(entity, dto);
        return toDto(incassoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<MorositaEditDto> listMorosita(Pageable pageable) {
        return morositaRepository.findAll(pageable).map(this::toDto);
    }

    public MorositaEditDto createMorosita(MorositaEditDto dto) {
        Morosita entity = new Morosita();
        applyMorosita(entity, dto);
        return toDto(morositaRepository.save(entity));
    }

    public void deleteMorosita(Long id) {
        Morosita entity = findMorosita(id);
        morositaRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public MorositaEditDto getMorosita(Long id) {
        return toDto(findMorosita(id));
    }

    public MorositaEditDto updateMorosita(Long id, MorositaEditDto dto) {
        Morosita entity = findMorosita(id);
        applyMorosita(entity, dto);
        return toDto(morositaRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<PianoCanoneEditDto> listPianiCanone(Pageable pageable) {
        return pianoCanoneRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CanoneEditDto> listCanoniPianoCanone(Pageable pageable, Long pianoCanoneId) {
        return canoneRepository.findByPianoCanoneId(pianoCanoneId, pageable).map(this::toDto);
    }

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntityEditingService.class);

    public PianoCanoneEditDto createPianoCanone(PianoCanoneEditDto dto) {
        PianoCanone entity = new PianoCanone();
        applyPianoCanone(entity, dto);
        // Dobbiamo creare i canoni dalla data inizio validità del piano canone fino
        // alla data fine validità, considerando la periodicità e la data di scadenza
        // indicati nel piano canone
        int monthShift = switch (entity.getPeriodicita()) {
            case MENSILE -> 1;
            case BIMESTRALE -> 2;
            case TRIMESTRALE -> 3;
            case QUADRIMESTRALE -> 4;
            case SEMESTRALE -> 6;
            case ANNUALE -> 12;
            default -> throw badRequest("Periodicità non supportata: " + entity.getPeriodicita());
        };
        LocalDate dataInizio = entity.getDataInizioValidita();
        ContrattoLocazione contratto = entity.getContrattoLocazione();
        if (contratto == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        if (dataInizio == null) {
            dataInizio = contratto.getDataInizio();
        }
        LocalDate dataFine = entity.getDataFineValidita(); // se non è indicata una data fine validità, consideriamo un periodo molto lungo
        PianoCanone savedPianoCanone = pianoCanoneRepository.save(entity);
        while (dataInizio.isBefore(dataFine)) {
            CanoneEditDto canoneDto = new CanoneEditDto();
            canoneDto.setPeriodoDa(dataInizio.withDayOfMonth(1));
            // verifica che il massimo del mese sia maggiore del giorno di scadenza,
            // altrimenti la data di scadenza sarà l'ultimo giorno del mese
            int giornoScadenza = entity.getGiornoScadenza() != null ? entity.getGiornoScadenza() : dataInizio.getDayOfMonth();
            int ultimoGiornoMese = dataInizio.lengthOfMonth();
            if (giornoScadenza > ultimoGiornoMese) {
                giornoScadenza = ultimoGiornoMese;
            }
            canoneDto.setDataScadenza(dataInizio.withDayOfMonth(giornoScadenza));
            LocalDate periodoA = canoneDto.getPeriodoDa().plusMonths(monthShift).minusDays(1);
            dataInizio = dataInizio.plusMonths(monthShift);
            canoneDto.setPeriodoA(periodoA);

            canoneDto.setImporto(entity.getImporto());
            canoneDto.setImportoIncassato(BigDecimal.ZERO);
            canoneDto.setTipo(entity.getTipo());
            canoneDto.setStato(StatoCanone.EMESSO);
            canoneDto.setContrattoLocazioneId(contratto.getId());
            canoneDto.setPianoCanoneId(savedPianoCanone.getId());

            Canone canone = new Canone();
            applyCanone(canone, canoneDto);
            canoneRepository.save(canone);
        }

        return toDto(savedPianoCanone);
    }

    public void deletePianoCanone(Long id) {
        PianoCanone entity = findPianoCanone(id);
        pianoCanoneRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public PianoCanoneEditDto getPianoCanone(Long id) {
        return toDto(findPianoCanone(id));
    }

    public PianoCanoneEditDto updatePianoCanone(Long id, PianoCanoneEditDto dto) {
        PianoCanone entity = findPianoCanone(id);
        applyPianoCanone(entity, dto);
        return toDto(pianoCanoneRepository.save(entity));
    }

    public PianoCanoneEditDto cancelPianoCanone(Long id, String username) {
        PianoCanone entity = findPianoCanone(id);
        cancelPianoCanone(entity, username);
        return toDto(pianoCanoneRepository.save(entity));
    }

    private void applyContrattoLocazione(ContrattoLocazione entity, ContrattoLocazioneEditDto dto) {
        entity.setDescrizione(dto.getDescrizione());
        entity.setIdImmobile(dto.getIdImmobile());
        entity.setIdUnita(dto.getIdUnita());
        entity.setIdConduttore(dto.getIdConduttore());
        entity.setCodiceContratto(dto.getCodiceContratto());
        entity.setDataInizio(dto.getDataInizio());
        entity.setDataFine(dto.getDataFine());
        entity.setDataPrimaScadenza(dto.getDataPrimaScadenza());
        entity.setDurataMesi(dto.getDurataMesi());
        entity.setCanoneBase(dto.getCanoneBase());
        entity.setPeriodicita(dto.getPeriodicita());
        entity.setRivalutazioneIstat(dto.getRivalutazioneIstat());
        entity.setDecorrenzaIstat(dto.getDecorrenzaIstat());
        entity.setPercentualeIstat(dto.getPercentualeIstat());
        entity.setDepositoCauzionale(dto.getDepositoCauzionale());
        entity.setStato(dto.getStato());
        entity.setDataCessazione(dto.getDataCessazione());
        entity.setNote(dto.getNote());
        entity.setTipologia(dto.getTipologia());
        entity.setTipologiaRinnovo(dto.getTipologiaRinnovo());
        entity.setMesiPreavviso(dto.getMesiPreavviso());
        entity.setSpeseAccessorieRiaddebitabili(dto.getSpeseAccessorieRiaddebitabili());
    }

    private void applyCanone(Canone entity, CanoneEditDto dto) {
        if (dto.getContrattoLocazioneId() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setContrattoLocazione(findContrattoLocazione(dto.getContrattoLocazioneId()));
        entity.setPianoCanone(dto.getPianoCanoneId() != null ? findPianoCanone(dto.getPianoCanoneId()) : null);
        entity.setPeriodoDa(dto.getPeriodoDa());
        entity.setPeriodoA(dto.getPeriodoA());
        entity.setDataScadenza(dto.getDataScadenza());
        entity.setImporto(dto.getImporto());
        entity.setImportoIncassato(dto.getImportoIncassato());
        entity.setTipo(dto.getTipo());
        entity.setStato(dto.getStato());
    }

    private void applyIncasso(Incasso entity, IncassoEditDto dto) {
        entity.setContrattoLocazione(dto.getContrattoLocazioneId() != null ? findContrattoLocazione(dto.getContrattoLocazioneId()) : null);
        entity.setCanone(dto.getCanoneId() != null ? findCanone(dto.getCanoneId()) : null);
        entity.setDataIncasso(dto.getDataIncasso());
        entity.setImporto(dto.getImporto());
        entity.setMetodo(dto.getMetodo());
        entity.setRiferimento(dto.getRiferimento());
        entity.setNote(dto.getNote());
    }

    private void applyMorosita(Morosita entity, MorositaEditDto dto) {
        if (dto.getContrattoLocazioneId() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setContrattoLocazione(findContrattoLocazione(dto.getContrattoLocazioneId()));
        entity.setCanone(dto.getCanoneId() != null ? findCanone(dto.getCanoneId()) : null);
        entity.setDataInizio(dto.getDataInizio());
        entity.setGiorniRitardo(dto.getGiorniRitardo());
        entity.setImportoResiduo(dto.getImportoResiduo());
        entity.setStato(dto.getStato());
        entity.setLivello(dto.getLivello());
        entity.setRiferimento(dto.getRiferimento());
        entity.setNote(dto.getNote());
    }

    private void applyPianoCanone(PianoCanone entity, PianoCanoneEditDto dto) {
        if (dto.getContrattoLocazioneId() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setContrattoLocazione(findContrattoLocazione(dto.getContrattoLocazioneId()));
        entity.setDataInizioValidita(dto.getDataInizioValidita());
        entity.setDataFineValidita(dto.getDataFineValidita());
        entity.setDataAnnullamento(dto.getDataAnnullamento());
        entity.setImporto(dto.getImporto());
        entity.setPeriodicita(dto.getPeriodicita());
        entity.setGiornoScadenza(dto.getGiornoScadenza());
        entity.setTipo(dto.getTipo());
        entity.setNote(dto.getNote());

    }

    private void cancelPianoCanone(PianoCanone entity, String username) {
        if (entity.getContrattoLocazione() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setDataAnnullamento(LocalDate.now());
        List<Canone> canoni = canoneRepository.findByPianoCanoneIdAndStato(entity.getId(), StatoCanone.EMESSO);
        for (Canone canone : canoni) {
            canone.setStato(StatoCanone.ANNULLATO);
            log.warn("Annullo canone {} con contratto {}", canone, canone.getContrattoLocazione());
            canoneRepository.save(canone);
        }
        eventoContrattoService.cancelPianoCanone(entity, username);

    }

    private ContrattoLocazione findContrattoLocazione(Long id) {
        if (id == null) {
            throw badRequest("L'id del ContrattoLocazione non può essere null");
        }
        return contrattoLocazioneRepository.findById(id)
                .orElseThrow(() -> notFound("ContrattoLocazione", id));
    }

    private ContrattoLocazione findContrattoLocazioneByCodice(String codice) {
        if (codice == null) {
            throw badRequest("Il codice del ContrattoLocazione non può essere null");
        }

        return contrattoLocazioneRepository.findByCodiceContratto(codice)
                .orElseThrow(() -> notFound("ContrattoLocazione", codice));
    }

    private List<PianoCanone> findContrattoLocazionePianiCanone(Long id) {
        List<PianoCanone> pianiCanone = pianoCanoneRepository.findByContrattoLocazioneId(id);
        if (pianiCanone.isEmpty()) {
            throw notFound("ContrattoLocazione", id);
        }
        return pianiCanone;
    }

    private Canone findCanone(Long id) {
        return canoneRepository.findById(id)
                .orElseThrow(() -> notFound("Canone", id));
    }

    private Incasso findIncasso(Long id) {
        return incassoRepository.findById(id)
                .orElseThrow(() -> notFound("Incasso", id));
    }

    private Morosita findMorosita(Long id) {
        return morositaRepository.findById(id)
                .orElseThrow(() -> notFound("Morosita", id));
    }

    private PianoCanone findPianoCanone(Long id) {
        return pianoCanoneRepository.findById(id)
                .orElseThrow(() -> notFound("PianoCanone", id));
    }

    private ResponseStatusException notFound(String entityName, Object id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + " non trovato con id=" + id);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ContrattoLocazioneEditDto toDto(ContrattoLocazione entity) {
        ContrattoLocazioneEditDto dto = new ContrattoLocazioneEditDto();
        dto.setId(entity.getId());
        dto.setDescrizione(entity.getDescrizione());
        dto.setIdImmobile(entity.getIdImmobile());
        dto.setIdUnita(entity.getIdUnita());
        dto.setIdConduttore(entity.getIdConduttore());
        dto.setCodiceContratto(entity.getCodiceContratto());
        dto.setDataInizio(entity.getDataInizio());
        dto.setDataFine(entity.getDataFine());
        dto.setDataPrimaScadenza(entity.getDataPrimaScadenza());
        dto.setDurataMesi(entity.getDurataMesi());
        dto.setCanoneBase(entity.getCanoneBase());
        dto.setPeriodicita(entity.getPeriodicita());
        dto.setRivalutazioneIstat(entity.getRivalutazioneIstat());
        dto.setPercentualeIstat(entity.getPercentualeIstat());
        dto.setDecorrenzaIstat(entity.getDecorrenzaIstat());
        dto.setDepositoCauzionale(entity.getDepositoCauzionale());
        dto.setStato(entity.getStato());
        dto.setDataCessazione(entity.getDataCessazione());
        dto.setNote(entity.getNote());
        dto.setTipologia(entity.getTipologia());
        dto.setTipologiaRinnovo(entity.getTipologiaRinnovo());
        dto.setMesiPreavviso(entity.getMesiPreavviso());
        dto.setSpeseAccessorieRiaddebitabili(entity.getSpeseAccessorieRiaddebitabili());
        return dto;
    }

    private CanoneEditDto toDto(Canone entity) {
        CanoneEditDto dto = new CanoneEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setPianoCanoneId(entity.getPianoCanone() != null ? entity.getPianoCanone().getId() : null);
        dto.setPeriodoDa(entity.getPeriodoDa());
        dto.setPeriodoA(entity.getPeriodoA());
        dto.setDataScadenza(entity.getDataScadenza());
        dto.setImporto(entity.getImporto());
        dto.setImportoIncassato(entity.getImportoIncassato());
        dto.setTipo(entity.getTipo());
        dto.setStato(entity.getStato());
        return dto;
    }

    private CanoneListDto toListDto(Canone entity) {
        ContrattoLocazione contratto = entity.getContrattoLocazione();
        CanoneListDto dto = new CanoneListDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(contratto != null ? contratto.getId() : null);
        dto.setIdUnita(contratto != null ? contratto.getIdUnita() : null);
        dto.setIdConduttore(contratto != null ? contratto.getIdConduttore() : null);
        dto.setCodiceContrattoLocazione(contratto != null ? contratto.getCodiceContratto() : null);
        dto.setDescrizioneContrattoLocazione(contratto != null ? contratto.getDescrizione() : null);
        dto.setCodiceContrattoLocazione(contratto != null ? contratto.getCodiceContratto() : null);
        dto.setPeriodoDa(entity.getPeriodoDa());
        dto.setPeriodoA(entity.getPeriodoA());
        dto.setDataScadenza(entity.getDataScadenza());
        dto.setImporto(entity.getImporto());
        dto.setImportoIncassato(entity.getImportoIncassato());
        dto.setTipo(entity.getTipo());
        dto.setStato(entity.getStato());
        return dto;
    }

    private IncassoEditDto toDto(Incasso entity) {
        IncassoEditDto dto = new IncassoEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setCanoneId(entity.getCanone() != null ? entity.getCanone().getId() : null);
        dto.setDataIncasso(entity.getDataIncasso());
        dto.setImporto(entity.getImporto());
        dto.setMetodo(entity.getMetodo());
        dto.setRiferimento(entity.getRiferimento());
        dto.setNote(entity.getNote());
        return dto;
    }

    private MorositaEditDto toDto(Morosita entity) {
        MorositaEditDto dto = new MorositaEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setCanoneId(entity.getCanone() != null ? entity.getCanone().getId() : null);
        dto.setDataInizio(entity.getDataInizio());
        dto.setGiorniRitardo(entity.getGiorniRitardo());
        dto.setImportoResiduo(entity.getImportoResiduo());
        dto.setStato(entity.getStato());
        dto.setLivello(entity.getLivello());
        dto.setRiferimento(entity.getRiferimento());
        dto.setNote(entity.getNote());
        return dto;
    }

    private PianoCanoneEditDto toDto(PianoCanone entity) {
        PianoCanoneEditDto dto = new PianoCanoneEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setDataInizioValidita(entity.getDataInizioValidita());
        dto.setDataFineValidita(entity.getDataFineValidita());
        dto.setDataAnnullamento(entity.getDataAnnullamento());
        dto.setImporto(entity.getImporto());
        dto.setPeriodicita(entity.getPeriodicita());
        dto.setGiornoScadenza(entity.getGiornoScadenza());
        dto.setTipo(entity.getTipo());
        dto.setNote(entity.getNote());
        return dto;
    }

    private EventoContrattoEditDto toDto(EventoContratto entity) {
        EventoContrattoEditDto dto = new EventoContrattoEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setTipoEvento(entity.getTipoEvento());
        dto.setDataEvento(entity.getDataEvento());
        dto.setRiferimentoTipo(entity.getRiferimentoTipo());
        dto.setRiferimentoId(entity.getRiferimentoId());
        dto.setPayloadJson(entity.getPayloadJson());
        dto.setNote(entity.getNote());
        dto.setCreatedBy(entity.getCreatedBy());
        return dto;
    }
}
