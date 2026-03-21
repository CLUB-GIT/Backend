package com.club.git.backend_app.repository;

import com.club.git.backend_app.entity.Membre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembreRepository extends JpaRepository<Membre, Integer> {

    Optional<Membre> findByEmail(String email);

    boolean existsByEmail(String email);
}
