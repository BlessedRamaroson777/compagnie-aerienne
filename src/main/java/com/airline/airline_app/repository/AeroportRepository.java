package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Aeroport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AeroportRepository extends JpaRepository<Aeroport, Long>, JpaSpecificationExecutor<Aeroport> {
    Optional<Aeroport> findByCodeAeroport(String codeAeroport);
}
