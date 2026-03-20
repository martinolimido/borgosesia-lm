package it.borgosesiaspa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Spesa;

@Repository
public interface SpesaRepository extends JpaRepository<Spesa, Long> {

    List<Spesa> findByContrattoLocazioneId(Long contrattoLocazioneId);

    Page<Spesa> findByContrattoLocazioneId(Long contrattoLocazioneId, Pageable pageable);
}
