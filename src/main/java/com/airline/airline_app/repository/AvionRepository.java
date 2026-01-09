package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Avion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvionRepository extends JpaRepository<Avion, Long>, JpaSpecificationExecutor<Avion> {
    Optional<Avion> findByCodeAvion(String codeAvion);
    boolean existsByCodeAvion(String codeAvion);
}
