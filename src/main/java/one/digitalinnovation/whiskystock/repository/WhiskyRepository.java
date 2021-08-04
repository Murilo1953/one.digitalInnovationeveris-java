package one.digitalinnovation.whiskystock.repository;

import one.digitalinnovation.whiskystock.entity.Whisky;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhiskyRepository extends JpaRepository<Whisky, Long> {

    Optional<Whisky> findByName (String name);
}
