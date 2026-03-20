package it.borgosesiaspa.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.ContrattoLocazione;
import it.borgosesiaspa.model.enums.ContrattoStato;

@Repository
public interface ContrattoLocazioneRepository extends JpaRepository<ContrattoLocazione, Long> {
    java.util.Optional<ContrattoLocazione> findByCodiceContratto(String codiceContratto);

    @org.springframework.data.jpa.repository.Query(
            value = "SELECT cl FROM ContrattoLocazione cl " +
                    "WHERE (:stato IS NULL OR cl.stato = :stato) " +
                    "AND (:dataInizioDa IS NULL OR cl.dataInizio >= :dataInizioDa) " +
                    "AND (:dataInizioA IS NULL OR cl.dataInizio <= :dataInizioA) " +
                    "AND (:dataFineDa IS NULL OR cl.dataFine >= :dataFineDa) " +
                    "AND (:dataFineA IS NULL OR cl.dataFine <= :dataFineA) " +
                    "AND (:idImmobile IS NULL OR cl.idImmobile = :idImmobile) " +
                    "AND (:idConduttore IS NULL OR cl.idConduttore = :idConduttore) " +
                    "AND (:azioneLegaleInCorso IS NULL OR cl.azioneLegaleInCorso = :azioneLegaleInCorso) " +
                    "AND (:dataProssimaRivalutazioneIstatDa IS NULL OR cl.dataProssimaRivalutazioneIstat >= :dataProssimaRivalutazioneIstatDa) " +
                    "AND (:dataProssimaRivalutazioneIstatA IS NULL OR cl.dataProssimaRivalutazioneIstat <= :dataProssimaRivalutazioneIstatA) " +
                    "AND (:tipologia IS NULL OR LOWER(cl.tipologia) = LOWER(:tipologia)) " +
                    "AND (:searchText IS NULL OR LOWER(COALESCE(cl.descrizione, '')) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
                    "OR LOWER(COALESCE(cl.note, '')) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<ContrattoLocazione> searchContratti(
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
            String searchText);
}
