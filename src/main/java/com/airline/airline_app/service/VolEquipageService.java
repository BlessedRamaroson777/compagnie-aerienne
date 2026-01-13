package com.airline.airline_app.service;

import com.airline.airline_app.entity.VolEquipage;
import com.airline.airline_app.repository.VolEquipageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolEquipageService extends GenericServiceImpl<VolEquipage, Long> {
    
    private final VolEquipageRepository volEquipageRepository;
    
    @Override
    protected JpaRepository<VolEquipage, Long> getRepository() {
        return volEquipageRepository;
    }
    
    @Override
    protected JpaSpecificationExecutor<VolEquipage> getSpecificationExecutor() {
        return volEquipageRepository;
    }
    
    public List<VolEquipage> findByVolProgrammeId(Long volProgrammeId) {
        return volEquipageRepository.findByVolProgrammeId(volProgrammeId);
    }
}
