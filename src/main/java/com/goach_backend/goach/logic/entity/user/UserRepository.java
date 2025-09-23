package com.goach_backend.goach.logic.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE %?1%")
    List<User> findUsersWithCharacterInName(String character);

    @Query("SELECT u FROM User u WHERE u.name = ?1")
    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);
}
