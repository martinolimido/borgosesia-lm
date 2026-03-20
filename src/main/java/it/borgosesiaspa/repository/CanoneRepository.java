package it.borgosesiaspa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Canone;
import it.borgosesiaspa.model.enums.StatoCanone;

@Repository
public interface CanoneRepository extends JpaRepository<Canone, Long> {

    List<Canone> findByContrattoLocazioneId(Long contrattoLocazioneId);

    List<Canone> findByPianoCanoneId(Long pianoCanoneId);

    Page<Canone> findByPianoCanoneId(Long pianoCanoneId, org.springframework.data.domain.Pageable pageable);

    List<Canone> findByPianoCanoneIdAndStatoNot(Long pianoCanoneId, StatoCanone notStato);

    List<Canone> findByPianoCanoneIdAndStato(Long pianoCanoneId, StatoCanone stato);

    @org.springframework.data.jpa.repository.Query(
            value = "SELECT DISTINCT c FROM Canone c " +
                    "LEFT JOIN c.contrattoLocazione cl " +
                    "LEFT JOIN cl.unita clu " +
                    "WHERE (:contrattoLocazioneId IS NULL OR cl.id = :contrattoLocazioneId) " +
                    "AND (:idImmobile IS NULL OR cl.idImmobile = :idImmobile) " +
                    "AND (:idUnita IS NULL OR clu.idUnita = :idUnita) " +
                    "AND (:pianoCanoneId IS NULL OR c.pianoCanone.id = :pianoCanoneId) " +
                    "AND (:scadenzaDa IS NULL OR c.dataScadenza >= :scadenzaDa) " +
                    "AND (:scadenzaA IS NULL OR c.dataScadenza <= :scadenzaA) " +
                    "AND (:statiCanone IS NULL OR c.stato IN :statiCanone)",
            countQuery = "SELECT COUNT(DISTINCT c.id) FROM Canone c " +
                    "LEFT JOIN c.contrattoLocazione cl " +
                    "LEFT JOIN cl.unita clu " +
                    "WHERE (:contrattoLocazioneId IS NULL OR cl.id = :contrattoLocazioneId) " +
                    "AND (:idImmobile IS NULL OR cl.idImmobile = :idImmobile) " +
                    "AND (:idUnita IS NULL OR clu.idUnita = :idUnita) " +
                    "AND (:pianoCanoneId IS NULL OR c.pianoCanone.id = :pianoCanoneId) " +
                    "AND (:scadenzaDa IS NULL OR c.dataScadenza >= :scadenzaDa) " +
                    "AND (:scadenzaA IS NULL OR c.dataScadenza <= :scadenzaA) " +
                    "AND (:statiCanone IS NULL OR c.stato IN :statiCanone)")
    Page<Canone> searchCanoni(org.springframework.data.domain.Pageable pageable, Long contrattoLocazioneId,
            Integer idImmobile,
            Integer idUnita,
            Long pianoCanoneId,
            LocalDate scadenzaDa,
            LocalDate scadenzaA,
            List<StatoCanone> statiCanone);

}
