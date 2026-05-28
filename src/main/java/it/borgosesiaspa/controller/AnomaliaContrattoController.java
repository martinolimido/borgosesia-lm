package it.borgosesiaspa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
import it.borgosesiaspa.dto.edit.AnomaliaContrattoStatoEditDto;
import it.borgosesiaspa.dto.read.AnomaliaContrattoReadDto;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;
import it.borgosesiaspa.model.enums.StatoAnomalia;
import it.borgosesiaspa.service.AnomaliaContrattoService;
import it.borgosesiaspa.service.verifica.ContrattoLocazioneVerificaScheduler;
import it.borgosesiaspa.service.verifica.RisultatoRun;

/**
 * Endpoint REST per consultare le anomalie rilevate dal task giornaliero di
 * verifica contratti, gestirne lo stato operativo (IGNORATA/APERTA) e
 * triggerare manualmente un run del task.
 */
@RestController
@RequestMapping("/api/lease-management/anomalie")
public class AnomaliaContrattoController {

    private final AnomaliaContrattoService anomaliaService;
    private final ContrattoLocazioneVerificaScheduler verificaScheduler;

    public AnomaliaContrattoController(
            AnomaliaContrattoService anomaliaService,
            ContrattoLocazioneVerificaScheduler verificaScheduler) {
        this.anomaliaService = anomaliaService;
        this.verificaScheduler = verificaScheduler;
    }

    @GetMapping
    @Operation(summary = "Elenca anomalie",
            description = "Restituisce l'elenco paginato delle anomalie rilevate dal task di verifica, filtrabile per contratto, stato, severità e codice.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elenco recuperato correttamente"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<PagedResponse<AnomaliaContrattoReadDto>> listAnomalie(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size,
            @RequestParam(required = false) Long contrattoLocazioneId,
            @RequestParam(required = false) StatoAnomalia stato,
            @RequestParam(required = false) SeveritaAnomalia severita,
            @RequestParam(required = false) CodiceAnomalia codice) {
        return ResponseEntity.ok(new PagedResponse<>(
                anomaliaService.searchAnomalie(
                        PageRequest.of(page, size),
                        contrattoLocazioneId,
                        stato,
                        severita,
                        codice)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dettaglio anomalia",
            description = "Restituisce una singola anomalia per id.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anomalia recuperata correttamente"),
            @ApiResponse(responseCode = "404", description = "Anomalia non trovata"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<AnomaliaContrattoReadDto> getAnomalia(@PathVariable Long id) {
        return ResponseEntity.ok(anomaliaService.getAnomalia(id));
    }

    @GetMapping("/contratto/{contrattoLocazioneId}")
    @Operation(summary = "Anomalie per contratto",
            description = "Restituisce tutte le anomalie associate a un contratto, opzionalmente filtrate per stato.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Elenco recuperato correttamente"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<List<AnomaliaContrattoReadDto>> getAnomalieContratto(
            @PathVariable Long contrattoLocazioneId,
            @RequestParam(required = false) StatoAnomalia stato) {
        return ResponseEntity.ok(anomaliaService.getAnomalieContratto(contrattoLocazioneId, stato));
    }

    @GetMapping("/conteggio-aperte-per-severita")
    @Operation(summary = "Conteggio anomalie aperte per severità",
            description = "Restituisce il numero di anomalie in stato APERTA aggregate per severità.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteggio recuperato correttamente"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<Map<SeveritaAnomalia, Long>> conteggioApertePerSeverita() {
        return ResponseEntity.ok(anomaliaService.conteggioApertePerSeverita());
    }

    @PutMapping("/{id}/ignora")
    @Operation(summary = "Ignora anomalia",
            description = "Marca un'anomalia come IGNORATA (silenziata). Resta consultabile ma esce dalle viste 'aperte'.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anomalia aggiornata"),
            @ApiResponse(responseCode = "404", description = "Anomalia non trovata"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<AnomaliaContrattoReadDto> ignoraAnomalia(
            @PathVariable Long id,
            @RequestBody(required = false) AnomaliaContrattoStatoEditDto dto) {
        return ResponseEntity.ok(anomaliaService.ignora(id, dto));
    }

    @PutMapping("/{id}/riapri")
    @Operation(summary = "Riapri anomalia",
            description = "Riporta un'anomalia IGNORATA o RISOLTA allo stato APERTA.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anomalia aggiornata"),
            @ApiResponse(responseCode = "404", description = "Anomalia non trovata"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<AnomaliaContrattoReadDto> riapriAnomalia(
            @PathVariable Long id,
            @RequestBody(required = false) AnomaliaContrattoStatoEditDto dto) {
        return ResponseEntity.ok(anomaliaService.riapri(id, dto));
    }

    @PostMapping("/verifica-manuale")
    @Operation(summary = "Esegui verifica on-demand",
            description = "Triggera manualmente un run del task di verifica contratti (utile in staging/admin).",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verifica eseguita"),
            @ApiResponse(responseCode = "401", description = "Non autorizzato")
    })
    public ResponseEntity<RisultatoRun> eseguiVerificaManuale() {
        return ResponseEntity.ok(verificaScheduler.esegui());
    }
}
