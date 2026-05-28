package it.borgosesiaspa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.AnomaliaContratto;
import it.borgosesiaspa.model.enums.CodiceAnomalia;
import it.borgosesiaspa.model.enums.SeveritaAnomalia;
import it.borgosesiaspa.model.enums.StatoAnomalia;

@Repository
public interface AnomaliaContrattoRepository extends JpaRepository<AnomaliaContratto, Long> {

    Optional<AnomaliaContratto> findFirstByHashFingerprintAndStato(String hashFingerprint, StatoAnomalia stato);

    List<AnomaliaContratto> findByContrattoLocazioneIdAndStato(Long contrattoLocazioneId, StatoAnomalia stato);

    List<AnomaliaContratto> findByContrattoLocazioneId(Long contrattoLocazioneId);

    long countByStato(StatoAnomalia stato);

    long countByStatoAndSeverita(StatoAnomalia stato, SeveritaAnomalia severita);

    @Query("SELECT a FROM AnomaliaContratto a " +
            "WHERE (:contrattoLocazioneId IS NULL OR a.contrattoLocazione.id = :contrattoLocazioneId) " +
            "AND (:stato IS NULL OR a.stato = :stato) " +
            "AND (:severita IS NULL OR a.severita = :severita) " +
            "AND (:codice IS NULL OR a.codice = :codice) " +
            "ORDER BY a.dataRilevazione DESC, a.id DESC")
    Page<AnomaliaContratto> searchAnomalie(
            Pageable pageable,
            Long contrattoLocazioneId,
            StatoAnomalia stato,
            SeveritaAnomalia severita,
            CodiceAnomalia codice);
}
