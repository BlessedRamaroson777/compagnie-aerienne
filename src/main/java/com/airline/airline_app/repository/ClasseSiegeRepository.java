package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ClasseSiege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClasseSiegeRepository extends JpaRepository<ClasseSiege, Long>, JpaSpecificationExecutor<ClasseSiege> {
    Optional<ClasseSiege> findByLibelle(String libelle);
}
