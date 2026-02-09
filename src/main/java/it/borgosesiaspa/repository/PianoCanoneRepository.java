package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.PianoCanone;
import java.util.List;

@Repository
public interface PianoCanoneRepository extends JpaRepository<PianoCanone, Long> {

    List<PianoCanone> findByContrattoLocazioneId(Long contrattoLocazioneId);
}
