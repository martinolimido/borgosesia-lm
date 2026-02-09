package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Morosita;
import java.util.List;

@Repository
public interface MorositaRepository extends JpaRepository<Morosita, Long> {

    List<Morosita> findByContrattoLocazioneId(Long contrattoLocazioneId);
}
