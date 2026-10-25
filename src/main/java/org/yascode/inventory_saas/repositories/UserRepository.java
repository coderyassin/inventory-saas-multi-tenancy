package org.yascode.inventory_saas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.yascode.inventory_saas.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdAndNotDeleted(String id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String adminUsername);

    boolean existsByEmail(String email);
}
