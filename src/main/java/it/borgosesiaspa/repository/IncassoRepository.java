package it.borgosesiaspa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.borgosesiaspa.model.Incasso;
import java.util.List;

@Repository
public interface IncassoRepository extends JpaRepository<Incasso, Long> {

    List<Incasso> findByContrattoLocazioneId(Long contrattoLocazioneId);
}
