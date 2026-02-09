package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.ContrattoLocazione;

@Repository
public interface ContrattoLocazioneRepository extends JpaRepository<ContrattoLocazione, Long> {

}
