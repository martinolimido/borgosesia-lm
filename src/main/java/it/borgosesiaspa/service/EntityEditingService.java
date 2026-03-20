package it.borgosesiaspa.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import it.borgosesiaspa.dto.edit.CanoneEditDto;
import it.borgosesiaspa.dto.edit.ContrattoCessazioneEditDto;
import it.borgosesiaspa.dto.edit.ContrattoLocazioneEditDto;
import it.borgosesiaspa.dto.edit.ContrattoLocazioneUnitaEditDto;
import it.borgosesiaspa.dto.edit.EventoContrattoEditDto;
import it.borgosesiaspa.dto.edit.IncassoEditDto;
import it.borgosesiaspa.dto.edit.MorositaEditDto;
import it.borgosesiaspa.dto.edit.PianoCanoneEditDto;
import it.borgosesiaspa.dto.edit.SpesaEditDto;
import it.borgosesiaspa.dto.read.CanoneReadOnlyDto;
import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.ContrattoLocazioneUnita;
import it.borgosesiaspa.model.EventoContratto;
import it.borgosesiaspa.model.Incasso;
import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.PianoCanone;
import it.borgosesiaspa.model.Spesa;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.Periodicita;
import it.borgosesiaspa.model.enums.StatoCanone;
import it.borgosesiaspa.model.enums.StatoMorosita;
import it.borgosesiaspa.repository.CanoneRepository;
import it.borgosesiaspa.repository.ContrattoLocazioneRepository;
import it.borgosesiaspa.repository.EventoContrattoRepository;
import it.borgosesiaspa.repository.IncassoRepository;
import it.borgosesiaspa.repository.MorositaRepository;
import it.borgosesiaspa.repository.PianoCanoneRepository;
import it.borgosesiaspa.repository.SpesaRepository;

@Service
@Transactional
public class EntityEditingService {

    private final ContrattoLocazioneRepository contrattoLocazioneRepository;
    private final CanoneRepository canoneRepository;
    private final EventoContrattoRepository eventoContrattoRepository;
    private final IncassoRepository incassoRepository;
    private final MorositaRepository morositaRepository;
    private final PianoCanoneRepository pianoCanoneRepository;
    private final SpesaRepository spesaRepository;
    // private final EventoContrattoRepository eventoContrattoRepository;
    private final EventoContrattoService eventoContrattoService;

    public EntityEditingService(
            ContrattoLocazioneRepository contrattoLocazioneRepository,
            CanoneRepository canoneRepository,
            EventoContrattoRepository eventoContrattoRepository,
            IncassoRepository incassoRepository,
            MorositaRepository morositaRepository,
            PianoCanoneRepository pianoCanoneRepository,
            SpesaRepository spesaRepository,
            EventoContrattoService eventoContrattoService) {
        this.contrattoLocazioneRepository = contrattoLocazioneRepository;
        this.canoneRepository = canoneRepository;
        this.eventoContrattoRepository = eventoContrattoRepository;
        this.incassoRepository = incassoRepository;
        this.morositaRepository = morositaRepository;
        this.pianoCanoneRepository = pianoCanoneRepository;
        this.spesaRepository = spesaRepository;
        this.eventoContrattoService = eventoContrattoService;
    }

    @Transactional(readOnly = true)
    public Page<ContrattoLocazioneEditDto> listContrattiLocazione(
            Pageable pageable,
            ContrattoStato stato,
            LocalDate dataInizioDa,
            LocalDate dataInizioA,
            LocalDate dataFineDa,
            LocalDate dataFineA,
            Integer idImmobile,
            Integer idConduttore,
            Boolean azioneLegaleInCorso,
            LocalDate dataProssimaRivalutazioneIstatDa,
            LocalDate dataProssimaRivalutazioneIstatA,
            String tipologia,
            String searchText) {
        return contrattoLocazioneRepository.searchContratti(
                pageable,
                stato,
                dataInizioDa,
                dataInizioA,
                dataFineDa,
                dataFineA,
                idImmobile,
                idConduttore,
                azioneLegaleInCorso,
                dataProssimaRivalutazioneIstatDa,
                dataProssimaRivalutazioneIstatA,
                tipologia,
                normalizeSearchText(searchText))
                .map(this::toDto);
    }

    public ContrattoLocazioneEditDto createContrattoLocazione(ContrattoLocazioneEditDto dto) {
        dto.setStato(ContrattoStato.ATTIVO);
        ContrattoLocazione entity = new ContrattoLocazione();
        applyContrattoLocazione(entity, dto);
        entity = contrattoLocazioneRepository.save(entity);
        entity.setCodiceContratto(buildCodiceContratto(entity.getId()));
        return toDto(contrattoLocazioneRepository.save(entity));
    }

    public void deleteContrattoLocazione(Long id) {
        ContrattoLocazione entity = findContrattoLocazione(id);
        contrattoLocazioneRepository.delete(entity);
    }

    public void deleteSpesa(Long id) {
        Spesa entity = findSpesa(id);
        spesaRepository.delete(entity);
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

    @Transactional(readOnly = true)
    public List<EventoContrattoEditDto> getContrattoLocazioneEventi(Long idContrattoLocazione) {
        findContrattoLocazione(idContrattoLocazione);
        return eventoContrattoRepository.findByContrattoLocazioneIdOrderByDataEventoDescIdDesc(idContrattoLocazione).stream()
                .map(this::toDto)
                .toList();
    }

    public ContrattoLocazioneEditDto updateContrattoLocazione(Long id, ContrattoLocazioneEditDto dto) {
        ContrattoLocazione entity = findContrattoLocazione(id);
        applyContrattoLocazione(entity, dto);
        return toDto(contrattoLocazioneRepository.save(entity));
    }

    public ContrattoLocazioneEditDto cessaContrattoLocazione(Long id, ContrattoCessazioneEditDto dto, String username) {
        if (dto == null || dto.getDataCessazione() == null) {
            throw badRequest("dataCessazione e' obbligatoria");
        }

        ContrattoLocazione entity = findContrattoLocazione(id);
        LocalDate dataCessazione = dto.getDataCessazione();
        List<PianoCanone> pianiAttivi = pianoCanoneRepository.findByContrattoLocazioneId(id).stream()
                .filter(piano -> isPianoCanoneAttivo(piano, dataCessazione))
                .toList();

        for (PianoCanone piano : pianiAttivi) {
            cancelPianoCanone(piano, dataCessazione, username);
            pianoCanoneRepository.save(piano);
        }

        entity.setDataCessazione(dataCessazione);
        entity.setStato(ContrattoStato.CESSATO);
        ContrattoLocazione saved = contrattoLocazioneRepository.save(entity);
        eventoContrattoService.cessaContratto(saved, pianiAttivi.size(), username);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<CanoneEditDto> listCanoni(Pageable pageable) {
        return canoneRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CanoneReadOnlyDto> searchCanoni(Pageable pageable, Long contrattoLocazioneId,
            Integer idImmobile,
            Integer idUnita,
            Long pianoCanoneId,
            LocalDate scadenzaDa,
            LocalDate scadenzaA,
            List<StatoCanone> statiCanone) {
        Page<Canone> entities = canoneRepository.searchCanoni(
                pageable,
                contrattoLocazioneId,
                idImmobile,
                idUnita,
                pianoCanoneId,
                scadenzaDa,
                scadenzaA,
                statiCanone);
        List<Long> canoneIds = entities.getContent().stream()
                .map(Canone::getId)
                .collect(Collectors.toList());
        Map<Long, MorositaOpenInfo> morositaByCanone = buildMorositaOpenInfoByCanoneId(canoneIds);
        return entities.map(entity -> toReadOnlyDto(entity, morositaByCanone.get(entity.getId())));
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
            BigDecimal diff = canoneDto.getImportoIncassato().setScale(2, RoundingMode.HALF_EVEN).subtract(canoneDto.getImporto().setScale(2, RoundingMode.HALF_EVEN));
            boolean incassoCompleto = diff.doubleValue() >= 0;
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
    public Page<SpesaEditDto> listSpese(Pageable pageable) {
        return spesaRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<SpesaEditDto> listSpeseContratto(Pageable pageable, Long contrattoLocazioneId) {
        return spesaRepository.findByContrattoLocazioneId(contrattoLocazioneId, pageable).map(this::toDto);
    }

    public SpesaEditDto createSpesa(SpesaEditDto dto) {
        Spesa entity = new Spesa();
        applySpesa(entity, dto);
        return toDto(spesaRepository.save(entity));
    }

    public SpesaEditDto updateSpesa(Long id, SpesaEditDto dto) {
        Spesa entity = findSpesa(id);
        applySpesa(entity, dto);
        return toDto(spesaRepository.save(entity));
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

    public MorositaEditDto chiudiMorosita(Long id, String username) {
        Morosita entity = findMorosita(id);
        if (entity.getStato() == StatoMorosita.CHIUSA) {
            throw badRequest("Morosita già chiusa");
        }
        entity.setStato(StatoMorosita.CHIUSA);
        entity = morositaRepository.save(entity);
        eventoContrattoService.chiudiMorosita(entity, username);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<PianoCanoneEditDto> listPianiCanone(Pageable pageable) {
        return pianoCanoneRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CanoneReadOnlyDto> listCanoniPianoCanone(Pageable pageable, Long pianoCanoneId) {
        var entities = canoneRepository.findByPianoCanoneId(pianoCanoneId, pageable);
        List<Long> canoneIds = entities.getContent().stream()
                .map(Canone::getId)
                .collect(Collectors.toList());
        Map<Long, MorositaOpenInfo> morositaByCanone = buildMorositaOpenInfoByCanoneId(canoneIds);
        return entities.map(entity -> toReadOnlyDto(entity, morositaByCanone.get(entity.getId())));

    }

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EntityEditingService.class);

    public PianoCanoneEditDto createPianoCanone(PianoCanoneEditDto dto, String username) {
        PianoCanone entity = new PianoCanone();
        applyPianoCanone(entity, dto);
        // Dobbiamo creare i canoni dalla data inizio validità del piano canone fino
        // alla data fine validità, considerando la periodicità e la data di scadenza
        // indicati nel piano canone
        var periodicita = entity.getPeriodicita() != null ? entity.getPeriodicita() : Periodicita.MENSILE;
        int monthShift = switch (periodicita) {
            case MENSILE -> 1;
            case BIMESTRALE -> 2;
            case TRIMESTRALE -> 3;
            case QUADRIMESTRALE -> 4;
            case SEMESTRALE -> 6;
            case ANNUALE -> 12;
            default -> throw badRequest("Periodicità non supportata: " + periodicita);
        };
        LocalDate dataInizio = entity.getDataInizioValidita();
        ContrattoLocazione contratto = entity.getContrattoLocazione();
        if (contratto == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        if (dataInizio == null) {
            dataInizio = contratto.getDataInizio();
        }
        log.info("Data inizio " + dataInizio);
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
        eventoContrattoService.createPianoCanone(entity, username);

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

    public PianoCanoneEditDto updatePianoCanone(Long id, PianoCanoneEditDto dto, String username) {
        PianoCanone entity = findPianoCanone(id);
        applyPianoCanone(entity, dto);
        // eventoContrattoService.updatePianoCanone(entity, username);
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
        entity.setIdConduttore(dto.getIdConduttore());
        entity.setSpeseAccessorieCaricoConduttore(dto.getSpeseAccessorieCaricoConduttore());
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
        entity.setAzioneLegaleInCorso(dto.getAzioneLegaleInCorso());
        entity.setCanoneVariabile(dto.getCanoneVariabile());
        entity.setDataProssimaRivalutazioneIstat(dto.getDataProssimaRivalutazioneIstat());
        syncContrattoUnita(entity, dto.getUnita());
    }

    private String buildCodiceContratto(Long id) {
        if (id == null) {
            throw badRequest("Impossibile generare il codice contratto senza id");
        }
        return "%d-%04d".formatted(LocalDate.now().getYear(), id);
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
        entity.setDataPrevisioneIncasso(dto.getDataPrevisioneIncasso());
        entity.setImportoResiduo(dto.getImportoResiduo());
        entity.setStato(dto.getStato());
        entity.setLivello(dto.getLivello());
        entity.setRiferimento(dto.getRiferimento());
        entity.setNote(dto.getNote());
    }

    private void applySpesa(Spesa entity, SpesaEditDto dto) {
        if (dto.getContrattoLocazioneId() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setContrattoLocazione(findContrattoLocazione(dto.getContrattoLocazioneId()));
        entity.setDataSpesa(dto.getDataSpesa());
        entity.setImporto(dto.getImporto());
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
        cancelPianoCanone(entity, LocalDate.now(), username);
    }

    private void cancelPianoCanone(PianoCanone entity, LocalDate dataAnnullamento, String username) {
        if (entity.getContrattoLocazione() == null) {
            throw badRequest("contrattoLocazioneId e' obbligatorio");
        }
        entity.setDataAnnullamento(dataAnnullamento);
        List<Canone> canoni = canoneRepository.findByPianoCanoneIdAndStato(entity.getId(), StatoCanone.EMESSO);
        for (Canone canone : canoni) {
            canone.setStato(StatoCanone.ANNULLATO);
            log.warn("Annullo canone {} con contratto {}", canone, canone.getContrattoLocazione());
            canoneRepository.save(canone);
        }
        eventoContrattoService.cancelPianoCanone(entity, dataAnnullamento, username);

    }

    private boolean isPianoCanoneAttivo(PianoCanone entity, LocalDate dataRiferimento) {
        if (entity.getDataAnnullamento() != null) {
            return false;
        }
        LocalDate dataInizio = entity.getDataInizioValidita();
        if (dataInizio != null && dataRiferimento.isBefore(dataInizio)) {
            return false;
        }
        LocalDate dataFine = entity.getDataFineValidita();
        return dataFine == null || !dataRiferimento.isAfter(dataFine);
    }

    private String normalizeSearchText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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

    private Spesa findSpesa(Long id) {
        return spesaRepository.findById(id)
                .orElseThrow(() -> notFound("Spesa", id));
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
        dto.setIdConduttore(entity.getIdConduttore());
        dto.setSpeseAccessorieCaricoConduttore(entity.getSpeseAccessorieCaricoConduttore());
        dto.setUnita(entity.getUnita().stream().map(this::toDto).toList());
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
        dto.setAzioneLegaleInCorso(entity.getAzioneLegaleInCorso());
        dto.setCanoneVariabile(entity.getCanoneVariabile());
        dto.setDataProssimaRivalutazioneIstat(entity.getDataProssimaRivalutazioneIstat());
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

    private CanoneReadOnlyDto toReadOnlyDto(Canone entity, MorositaOpenInfo morositaOpenInfo) {
        ContrattoLocazione contratto = entity.getContrattoLocazione();
        long numeroMorositaAperte = morositaOpenInfo != null ? morositaOpenInfo.count() : 0L;
        Long idUltimaMorositaAperta = morositaOpenInfo != null ? morositaOpenInfo.lastOpenMorositaId() : null;
        BigDecimal importoInsoluto = morositaOpenInfo != null ? morositaOpenInfo.totalOpenAmount() : BigDecimal.ZERO;
        return new CanoneReadOnlyDto(
                entity.getId(),
                contratto != null
                        ? contratto.getUnita().stream().map(ContrattoLocazioneUnita::getIdUnita).toList()
                        : List.of(),
                contratto != null ? contratto.getIdImmobile() : null,
                contratto != null ? contratto.getIdConduttore() : null,
                contratto != null ? contratto.getId() : null,
                entity.getPianoCanone() != null ? entity.getPianoCanone().getId() : null,
                contratto != null ? contratto.getCodiceContratto() : null,
                contratto != null ? contratto.getDescrizione() : null,
                entity.getPeriodoDa(),
                entity.getPeriodoA(),
                entity.getDataScadenza(),
                entity.getImporto(),
                entity.getImportoIncassato(),
                entity.getTipo(),
                entity.getStato(),
                numeroMorositaAperte,
                idUltimaMorositaAperta,
                importoInsoluto);
    }

    private void syncContrattoUnita(ContrattoLocazione entity, List<ContrattoLocazioneUnitaEditDto> dtoUnita) {
        List<ContrattoLocazioneUnitaEditDto> requestedUnita = dtoUnita != null ? dtoUnita : List.of();
        Map<Long, ContrattoLocazioneUnita> existingById = entity.getUnita().stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(ContrattoLocazioneUnita::getId, item -> item));
        Set<ContrattoLocazioneUnita> retained = new HashSet<>();

        for (ContrattoLocazioneUnitaEditDto itemDto : requestedUnita) {
            if (itemDto.getIdUnita() == null) {
                continue;
            }
            ContrattoLocazioneUnita item = findExistingContrattoUnita(entity.getUnita(), existingById, itemDto, retained);
            if (item == null) {
                item = new ContrattoLocazioneUnita();
                entity.addUnita(item);
            }
            item.setIdUnita(itemDto.getIdUnita());
            retained.add(item);
        }

        entity.getUnita().removeIf(item -> !retained.contains(item));
    }

    private ContrattoLocazioneUnita findExistingContrattoUnita(
            List<ContrattoLocazioneUnita> existingUnita,
            Map<Long, ContrattoLocazioneUnita> existingById,
            ContrattoLocazioneUnitaEditDto itemDto,
            Set<ContrattoLocazioneUnita> retained) {
        if (itemDto.getId() != null) {
            ContrattoLocazioneUnita existing = existingById.get(itemDto.getId());
            if (existing != null) {
                return existing;
            }
        }
        for (ContrattoLocazioneUnita existing : existingUnita) {
            if (retained.contains(existing)) {
                continue;
            }
            if (existing.getIdUnita() != null && existing.getIdUnita().equals(itemDto.getIdUnita())) {
                return existing;
            }
        }
        return null;
    }

    private ContrattoLocazioneUnitaEditDto toDto(ContrattoLocazioneUnita entity) {
        ContrattoLocazioneUnitaEditDto dto = new ContrattoLocazioneUnitaEditDto();
        dto.setId(entity.getId());
        dto.setIdUnita(entity.getIdUnita());
        return dto;
    }

    private Map<Long, MorositaOpenInfo> buildMorositaOpenInfoByCanoneId(List<Long> canoneIds) {
        if (canoneIds == null || canoneIds.isEmpty()) {
            return Map.of();
        }
        List<Morosita> morositaAperte = morositaRepository.findByCanoneIdInAndStatoOrderByIdDesc(canoneIds, StatoMorosita.APERTA);
        Map<Long, MorositaOpenInfo> result = new HashMap<>();
        for (Morosita morosita : morositaAperte) {
            if (morosita.getCanone() == null || morosita.getCanone().getId() == null) {
                continue;
            }
            Long canoneId = morosita.getCanone().getId();
            MorositaOpenInfo current = result.get(canoneId);
            BigDecimal importoResiduo = morosita.getImportoResiduo() != null ? morosita.getImportoResiduo() : BigDecimal.ZERO;
            if (current == null) {
                result.put(canoneId, new MorositaOpenInfo(1L, morosita.getId(), importoResiduo));
            } else {
                result.put(canoneId, new MorositaOpenInfo(
                        current.count() + 1L,
                        current.lastOpenMorositaId(),
                        current.totalOpenAmount().add(importoResiduo)));
            }
        }
        return result;
    }

    private record MorositaOpenInfo(long count, Long lastOpenMorositaId, BigDecimal totalOpenAmount) {
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
        dto.setDataPrevisioneIncasso(entity.getDataPrevisioneIncasso());
        dto.setImportoResiduo(entity.getImportoResiduo());
        dto.setStato(entity.getStato());
        dto.setLivello(entity.getLivello());
        dto.setRiferimento(entity.getRiferimento());
        dto.setNote(entity.getNote());
        return dto;
    }

    private SpesaEditDto toDto(Spesa entity) {
        SpesaEditDto dto = new SpesaEditDto();
        dto.setId(entity.getId());
        dto.setContrattoLocazioneId(entity.getContrattoLocazione() != null ? entity.getContrattoLocazione().getId() : null);
        dto.setDataSpesa(entity.getDataSpesa());
        dto.setImporto(entity.getImporto());
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
