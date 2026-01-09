package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Equipage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipageRepository extends JpaRepository<Equipage, Long>, JpaSpecificationExecutor<Equipage> {
}
