package com.airline.airline_app.repository;

import com.airline.airline_app.entity.AvionClasseConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvionClasseConfigurationRepository extends JpaRepository<AvionClasseConfiguration, Long> {
    
    List<AvionClasseConfiguration> findByAvionId(Long avionId);
    
    Optional<AvionClasseConfiguration> findByAvionIdAndClasseSiegeId(Long avionId, Long classeSiegeId);
    
    @Query("SELECT SUM(acc.nombrePlaces) FROM AvionClasseConfiguration acc WHERE acc.avion.id = :avionId")
    Integer getTotalCapaciteByAvionId(@Param("avionId") Long avionId);
    
    void deleteByAvionId(Long avionId);
}
