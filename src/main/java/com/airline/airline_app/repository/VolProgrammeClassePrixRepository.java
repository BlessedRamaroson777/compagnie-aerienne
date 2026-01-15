package com.airline.airline_app.repository;

import com.airline.airline_app.entity.VolProgrammeClassePrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolProgrammeClassePrixRepository extends JpaRepository<VolProgrammeClassePrix, Long> {
    
    List<VolProgrammeClassePrix> findByVolProgrammeId(Long volProgrammeId);
    
    Optional<VolProgrammeClassePrix> findByVolProgrammeIdAndClasseSiegeId(Long volProgrammeId, Long classeSiegeId);
    
    void deleteByVolProgrammeId(Long volProgrammeId);
    
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VolProgrammeClassePrix v " +
           "WHERE v.volProgramme.id = :volProgrammeId AND v.classeSiege.id = :classeSiegeId")
    boolean existsByVolProgrammeIdAndClasseSiegeId(Long volProgrammeId, Long classeSiegeId);
}
