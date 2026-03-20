package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Morosita;
import it.borgosesiaspa.model.enums.StatoMorosita;

import java.util.List;

@Repository
public interface MorositaRepository extends JpaRepository<Morosita, Long> {

    List<Morosita> findByContrattoLocazioneId(Long contrattoLocazioneId);

    List<Morosita> findByCanoneIdInAndStatoOrderByIdDesc(List<Long> canoneIds, StatoMorosita stato);
}
