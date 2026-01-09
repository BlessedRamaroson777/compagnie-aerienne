package com.airline.airline_app.repository;

import com.airline.airline_app.entity.VolEquipage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VolEquipageRepository extends JpaRepository<VolEquipage, Long>, JpaSpecificationExecutor<VolEquipage> {
}
