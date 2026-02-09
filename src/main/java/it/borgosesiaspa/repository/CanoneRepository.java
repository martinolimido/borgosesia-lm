package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Canone;
import java.util.List;

@Repository
public interface CanoneRepository extends JpaRepository<Canone, Long> {

    List<Canone> findByContrattoLocazioneId(Long contrattoLocazioneId);
}
