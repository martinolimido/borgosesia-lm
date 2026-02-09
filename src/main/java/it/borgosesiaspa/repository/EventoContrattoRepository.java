package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.EventoContratto;
import java.util.List;

@Repository
public interface EventoContrattoRepository extends JpaRepository<EventoContratto, Long> {

    List<EventoContratto> findByContrattoLocazioneId(Long contrattoLocazioneId);
}
