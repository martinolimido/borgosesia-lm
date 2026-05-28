package it.borgosesiaspa.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.borgosesiaspa.dto.PagedResponse;
import it.borgosesiaspa.dto.edit.CanoneEditDto;
import it.borgosesiaspa.dto.edit.ContrattoCessazioneEditDto;
import it.borgosesiaspa.dto.edit.ContrattoLocazioneEditDto;
import it.borgosesiaspa.dto.edit.EventoContrattoEditDto;
import it.borgosesiaspa.dto.edit.IncassoEditDto;
import it.borgosesiaspa.dto.edit.MorositaEditDto;
import it.borgosesiaspa.dto.edit.PianoCanoneEditDto;
import it.borgosesiaspa.dto.edit.SpesaEditDto;
import it.borgosesiaspa.dto.read.CanoneReadOnlyDto;
import it.borgosesiaspa.model.enums.ContrattoStato;
import it.borgosesiaspa.model.enums.StatoCanone;
import it.borgosesiaspa.service.EntityEditingService;

@RestController
@RequestMapping("/api/lease-management")
public class EntityEditingController {

        private final EntityEditingService entityEditingService;

        public EntityEditingController(EntityEditingService entityEditingService) {
                this.entityEditingService = entityEditingService;
        }

        @GetMapping("/contratti")
        @Operation(summary = "Elenca contratti", description = "Restituisce l'elenco dei contratti di locazione.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco contratti recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<ContrattoLocazioneEditDto>> listContrattiLocazione(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size,
                        @RequestParam(required = false) ContrattoStato stato,
                        @RequestParam(required = false) LocalDate dataInizioDa,
                        @RequestParam(required = false) LocalDate dataInizioA,
                        @RequestParam(required = false) LocalDate dataFineDa,
                        @RequestParam(required = false) LocalDate dataFineA,
                        @RequestParam(required = false) Integer idImmobile,
                        @RequestParam(required = false) Integer idConduttore,
                        @RequestParam(required = false) Boolean azioneLegaleInCorso,
                        @RequestParam(required = false) LocalDate dataProssimaRivalutazioneIstatDa,
                        @RequestParam(required = false) LocalDate dataProssimaRivalutazioneIstatA,
                        @RequestParam(required = false) String tipologia,
                        @RequestParam(required = false) String searchText) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listContrattiLocazione(
                                                PageRequest.of(page, size),
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
                                                searchText)));
        }

        @PostMapping("/contratti")
        @Operation(summary = "Crea contratto", description = "Crea un nuovo contratto di locazione.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contratto creato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<ContrattoLocazioneEditDto> createContrattoLocazione(
                        @RequestBody ContrattoLocazioneEditDto dto) {
                return ResponseEntity.ok(entityEditingService.createContrattoLocazione(dto));
        }

        @GetMapping("/contratti/{id}")
        @Operation(summary = "Dettaglio contratto", description = "Restituisce un singolo contratto di locazione per id.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contratto recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<ContrattoLocazioneEditDto> getContrattoLocazione(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getContrattoLocazione(id));
        }

        @GetMapping("/contratto-per-codice")
        @Operation(summary = "Dettaglio contratto", description = "Restituisce un singolo contratto di locazione per codice.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contratto recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<List<ContrattoLocazioneEditDto>> getContrattoLocazioneByCodice(@RequestParam String codice) {
                try {
                        return ResponseEntity.ok(List.of(entityEditingService.getContrattoLocazioneByCodice(codice)));
                } catch (Exception e) {
                        return ResponseEntity.ok(List.of());
                }
        }

        @GetMapping("/contratti/{id}/piani-canone")
        @Operation(summary = "Elenco piani canone contratto", description = "Restituisce l'elenco dei piani canone per un contratto di locazione.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco piani canone recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<List<PianoCanoneEditDto>> getContrattoLocazionePianiCanone(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getContrattoLocazionePianiCanone(id));
        }

        @GetMapping("/contratti/{id}/eventi")
        @Operation(summary = "Elenco eventi contratto", description = "Restituisce l'elenco degli eventi per un contratto di locazione.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco eventi recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<List<EventoContrattoEditDto>> getContrattoLocazioneEventi(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getContrattoLocazioneEventi(id));
        }

        @GetMapping("/contratti/{id}/spese")
        @Operation(summary = "Elenco spese contratto", description = "Restituisce l'elenco delle spese per un contratto di locazione.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco spese recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<SpesaEditDto>> listSpeseContratto(
                        @PathVariable Long id,
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listSpeseContratto(PageRequest.of(page, size), id)));
        }

        @PutMapping("/contratti/{id}")
        @Operation(summary = "Aggiorna contratto", description = "Aggiorna un contratto di locazione esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contratto aggiornato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<ContrattoLocazioneEditDto> updateContrattoLocazione(
                        @PathVariable Long id,
                        @RequestBody ContrattoLocazioneEditDto dto) {
                return ResponseEntity.ok(entityEditingService.updateContrattoLocazione(id, dto));
        }

        @PutMapping("/contratti/{id}/cessa")
        @Operation(summary = "Cessa contratto", description = "Cessa un contratto di locazione, annullando i piani canone attivi.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contratto cessato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<ContrattoLocazioneEditDto> cessaContrattoLocazione(
                        @PathVariable Long id,
                        @RequestBody ContrattoCessazioneEditDto dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.cessaContrattoLocazione(id, dto, username));
        }

        @DeleteMapping("/contratti/{id}")
        @Operation(summary = "Cancella contratto", description = "Cancella un contratto di locazione esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Contratto cancellato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Contratto non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deleteContrattoLocazione(@PathVariable Long id) {
                entityEditingService.deleteContrattoLocazione(id);
                return ResponseEntity.noContent().build();
        }

        /**
         * Campi ordinabili per i canoni. Corrisponde alle proprietà JPA di
         * {@link it.borgosesiaspa.model.Canone} che ha senso esporre come sort key.
         */
        private static final java.util.Set<String> CANONE_SORT_FIELDS = java.util.Set.of(
                        "dataScadenza", "periodoDa", "periodoA", "importo", "importoIncassato", "stato", "id");

        @GetMapping("/canoni")
        @Operation(summary = "Cerca i canoni", description = """
                        Restituisce l'elenco dei canoni con paginazione e ordinamento configurabile.
                        Campi ordinabili: dataScadenza, periodoDa, periodoA, importo, importoIncassato, stato, id.
                        Default: dataScadenza ASC.""", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco canoni recuperato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Campo di ordinamento non valido"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<CanoneReadOnlyDto>> listCanoni(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size,
                        @RequestParam(required = false) Long contrattoLocazioneId,
                        @RequestParam(required = false) Integer idImmobile,
                        @RequestParam(required = false) Integer idUnita,
                        @RequestParam(required = false) Long pianoCanoneId,
                        @RequestParam(required = false) LocalDate scadenzaDa,
                        @RequestParam(required = false) LocalDate scadenzaA,
                        @RequestParam(required = false) List<StatoCanone> statiCanone,
                        @RequestParam(required = false, defaultValue = "dataScadenza") String sortBy,
                        @RequestParam(required = false, defaultValue = "asc") String sortDir) {
                if (!CANONE_SORT_FIELDS.contains(sortBy)) {
                        throw new org.springframework.web.server.ResponseStatusException(
                                        org.springframework.http.HttpStatus.BAD_REQUEST,
                                        "Campo di ordinamento non valido: '" + sortBy
                                                        + "'. Valori ammessi: " + CANONE_SORT_FIELDS);
                }
                Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Sort sort = Sort.by(direction, sortBy);
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.searchCanoni(PageRequest.of(page, size, sort),
                                                contrattoLocazioneId, idImmobile, idUnita, pianoCanoneId,
                                                scadenzaDa, scadenzaA, statiCanone)));
        }

        @PostMapping("/canoni")
        @Operation(summary = "Crea canone", description = "Crea un nuovo canone.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Canone creato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<CanoneEditDto> createCanone(@RequestBody CanoneEditDto dto) {
                return ResponseEntity.ok(entityEditingService.createCanone(dto));
        }

        @PostMapping("/canoni/{id}/registra-incasso")
        @Operation(summary = "Crea incasso", description = "Crea un nuovo incasso per un canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Incasso creato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<IncassoEditDto> registraIncassoCanone(@PathVariable Long id, @RequestBody IncassoEditDto dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.registraIncassoCanone(id, dto, username));
        }

        @PostMapping("/canoni/{id}/apri-morosita")
        @Operation(summary = "Apri morosità", description = "Apri la morosità per un canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Morosità aperta correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<MorositaEditDto> apriMorositaCanone(@PathVariable Long id, @RequestBody MorositaEditDto dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.apriMorositaCanone(id, dto, username));
        }

        @GetMapping("/canoni/{id}")
        @Operation(summary = "Dettaglio canone", description = "Restituisce un singolo canone per id.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Canone recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<CanoneEditDto> getCanone(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getCanone(id));
        }

        @PutMapping("/canoni/{id}")
        @Operation(summary = "Aggiorna canone", description = "Aggiorna un canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Canone aggiornato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<CanoneEditDto> updateCanone(
                        @PathVariable Long id,
                        @RequestBody CanoneEditDto dto) {
                return ResponseEntity.ok(entityEditingService.updateCanone(id, dto));
        }

        @DeleteMapping("/canoni/{id}")
        @Operation(summary = "Cancella canone", description = "Cancella un canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Canone cancellato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deleteCanone(@PathVariable Long id) {
                entityEditingService.deleteCanone(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/incassi")
        @Operation(summary = "Elenca incassi", description = "Restituisce l'elenco degli incassi.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco incassi recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<IncassoEditDto>> listIncassi(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listIncassi(PageRequest.of(page, size))));
        }

        @PostMapping("/incassi")
        @Operation(summary = "Crea incasso", description = "Crea un nuovo incasso.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Incasso creato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<IncassoEditDto> createIncasso(@RequestBody IncassoEditDto dto) {
                return ResponseEntity.ok(entityEditingService.createIncasso(dto));
        }

        @GetMapping("/incassi/{id}")
        @Operation(summary = "Dettaglio incasso", description = "Restituisce un singolo incasso per id.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Incasso recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Incasso non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<IncassoEditDto> getIncasso(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getIncasso(id));
        }

        @PutMapping("/incassi/{id}")
        @Operation(summary = "Aggiorna incasso", description = "Aggiorna un incasso esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Incasso aggiornato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Incasso non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<IncassoEditDto> updateIncasso(
                        @PathVariable Long id,
                        @RequestBody IncassoEditDto dto) {
                return ResponseEntity.ok(entityEditingService.updateIncasso(id, dto));
        }

        @DeleteMapping("/incassi/{id}")
        @Operation(summary = "Cancella incasso", description = "Cancella un incasso esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Incasso cancellato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Incasso non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deleteIncasso(@PathVariable Long id) {
                entityEditingService.deleteIncasso(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/spese")
        @Operation(summary = "Elenca spese", description = "Restituisce l'elenco delle spese accessorie sostenute per i contratti.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco spese recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<SpesaEditDto>> listSpese(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listSpese(PageRequest.of(page, size))));
        }

        @PostMapping("/spese")
        @Operation(summary = "Crea spesa", description = "Crea una nuova spesa accessoria in capo al contratto.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Spesa creata correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<SpesaEditDto> createSpesa(@RequestBody SpesaEditDto dto) {
                return ResponseEntity.ok(entityEditingService.createSpesa(dto));
        }

        @PutMapping("/spese/{id}")
        @Operation(summary = "Aggiorna spesa", description = "Aggiorna una spesa accessoria esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Spesa aggiornata correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Spesa non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<SpesaEditDto> updateSpesa(
                        @PathVariable Long id,
                        @RequestBody SpesaEditDto dto) {
                return ResponseEntity.ok(entityEditingService.updateSpesa(id, dto));
        }

        @DeleteMapping("/spese/{id}")
        @Operation(summary = "Cancella spesa", description = "Cancella una spesa accessoria esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Spesa cancellata correttamente"),
                        @ApiResponse(responseCode = "404", description = "Spesa non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deleteSpesa(@PathVariable Long id) {
                entityEditingService.deleteSpesa(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/morosita")
        @Operation(summary = "Elenca morosita", description = "Restituisce l'elenco delle morosita.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco morosita recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<MorositaEditDto>> listMorosita(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listMorosita(PageRequest.of(page, size))));
        }

        @PostMapping("/morosita")
        @Operation(summary = "Crea morosita", description = "Crea una nuova morosita.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Morosita creata correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<MorositaEditDto> createMorosita(@RequestBody MorositaEditDto dto) {
                return ResponseEntity.ok(entityEditingService.createMorosita(dto));
        }

        @GetMapping("/morosita/{id}")
        @Operation(summary = "Dettaglio morosita", description = "Restituisce una singola morosita per id.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Morosita recuperata correttamente"),
                        @ApiResponse(responseCode = "404", description = "Morosita non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<MorositaEditDto> getMorosita(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getMorosita(id));
        }

        @PutMapping("/morosita/{id}")
        @Operation(summary = "Aggiorna morosita", description = "Aggiorna una morosita esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Morosita aggiornata correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Morosita non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<MorositaEditDto> updateMorosita(
                        @PathVariable Long id,
                        @RequestBody MorositaEditDto dto) {
                return ResponseEntity.ok(entityEditingService.updateMorosita(id, dto));
        }

        @PostMapping("/morosita/{id}/chiudi")
        @Operation(summary = "Chiudi morosita", description = "Chiude una morosita esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Morosita chiusa correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Morosita non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<MorositaEditDto> chiudiMorosita(@PathVariable Long id) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.chiudiMorosita(id, username));
        }

        @DeleteMapping("/morosita/{id}")
        @Operation(summary = "Cancella morosita", description = "Cancella una morosita esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Morosita cancellata correttamente"),
                        @ApiResponse(responseCode = "404", description = "Morosita non trovata"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deleteMorosita(@PathVariable Long id) {
                entityEditingService.deleteMorosita(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/piani-canone")
        @Operation(summary = "Elenca piani canone", description = "Restituisce l'elenco dei piani canone.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco piani canone recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<PianoCanoneEditDto>> listPianiCanone(
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listPianiCanone(PageRequest.of(page, size))));
        }

        @GetMapping("/piani-canone/{id}/canoni")
        @Operation(summary = "Elenca canoni piano canone", description = "Restituisce l'elenco dei canoni per un piano canone.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Elenco canoni recuperato correttamente"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PagedResponse<CanoneReadOnlyDto>> listCanoniPianoCanone(
                        @PathVariable Long id,
                        @RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "50") int size) {
                return ResponseEntity.ok(new PagedResponse<>(
                                entityEditingService.listCanoniPianoCanone(PageRequest.of(page, size), id)));
        }

        @PostMapping("/piani-canone")
        @Operation(summary = "Crea piano canone", description = "Crea un nuovo piano canone.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piano canone creato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PianoCanoneEditDto> createPianoCanone(@RequestBody PianoCanoneEditDto dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.createPianoCanone(dto, username));
        }

        @GetMapping("/piani-canone/{id}")
        @Operation(summary = "Dettaglio piano canone", description = "Restituisce un singolo piano canone per id.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piano canone recuperato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Piano canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PianoCanoneEditDto> getPianoCanone(@PathVariable Long id) {
                return ResponseEntity.ok(entityEditingService.getPianoCanone(id));
        }

        @PutMapping("/piani-canone/{id}")
        @Operation(summary = "Aggiorna piano canone", description = "Aggiorna un piano canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piano canone aggiornato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Piano canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PianoCanoneEditDto> updatePianoCanone(
                        @PathVariable Long id,
                        @RequestBody PianoCanoneEditDto dto) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.updatePianoCanone(id, dto, username));
        }

        @PutMapping("/piani-canone/{id}/annulla")
        @Operation(summary = "Annulla piano canone", description = "Annulla un piano canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piano canone annullato correttamente"),
                        @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
                        @ApiResponse(responseCode = "404", description = "Piano canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<PianoCanoneEditDto> cancelPianoCanone(
                        @PathVariable Long id) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return ResponseEntity.ok(entityEditingService.cancelPianoCanone(id, username));
        }

        @DeleteMapping("/piani-canone/{id}")
        @Operation(summary = "Cancella piano canone", description = "Cancella un piano canone esistente.", security = @SecurityRequirement(name = "BearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Piano canone cancellato correttamente"),
                        @ApiResponse(responseCode = "404", description = "Piano canone non trovato"),
                        @ApiResponse(responseCode = "401", description = "Non autorizzato")
        })
        public ResponseEntity<Void> deletePianoCanone(@PathVariable Long id) {
                entityEditingService.deletePianoCanone(id);
                return ResponseEntity.noContent().build();
        }
}
