package com.airline.airline_app.service;

import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.repository.VolProgrammeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VolProgrammeService extends GenericServiceImpl<VolProgramme, Long> {

    private final VolProgrammeRepository volProgrammeRepository;

    @Override
    protected JpaRepository<VolProgramme, Long> getRepository() {
        return volProgrammeRepository;
    }

    @Override
    protected JpaSpecificationExecutor<VolProgramme> getSpecificationExecutor() {
        return volProgrammeRepository;
    }

    public List<VolProgramme> findByVolId(Long volId) {
        return volProgrammeRepository.findByVolId(volId);
    }

    public List<VolProgramme> findByAeroportsAndDate(Long aeroportDepartId, Long aeroportArriveeId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return volProgrammeRepository.findByVolAeroportDepartIdAndVolAeroportArriveeIdAndDateHeureDepartBetween(
                aeroportDepartId, aeroportArriveeId, start, end);
    }
}
