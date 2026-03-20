package it.borgosesiaspa.dto.read;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import it.borgosesiaspa.model.enums.StatoCanone;
import it.borgosesiaspa.model.enums.TipoCanone;

public record CanoneReadOnlyDto(
        Long id,
        List<Integer> idUnita,
        Integer idImmobile,
        Integer idConduttore,
        Long contrattoLocazioneId,
        Long pianoCanoneId,
        String codiceContrattoLocazione,
        String descrizioneContrattoLocazione,
        LocalDate periodoDa,
        LocalDate periodoA,
        LocalDate dataScadenza,
        BigDecimal importo,
        BigDecimal importoIncassato,
        TipoCanone tipo,
        StatoCanone stato,
        long numeroMorositaAperte,
        Long idUltimaMorositaAperta,
        BigDecimal importoInsoluto) {
}
