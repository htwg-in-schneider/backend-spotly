package de.spotly.backend.repository;

import de.spotly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Hier kannst du später noch Methoden wie findByUsername hinzufügen
}