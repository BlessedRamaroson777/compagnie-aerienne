package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ModelAvion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelAvionRepository extends JpaRepository<ModelAvion, Long>, JpaSpecificationExecutor<ModelAvion> {
    Optional<ModelAvion> findByDesignation(String designation);
}
