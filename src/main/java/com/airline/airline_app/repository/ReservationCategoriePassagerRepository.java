package com.airline.airline_app.repository;

import com.airline.airline_app.entity.ReservationCategoriePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationCategoriePassagerRepository extends JpaRepository<ReservationCategoriePassager, Long> {
    List<ReservationCategoriePassager> findByReservation_Id(Long reservationId);
    List<ReservationCategoriePassager> findByCategoriePassager_IdCategoriePassager(Long categorieId);
    List<ReservationCategoriePassager> findByClasseSiege_Id(Long classeId);
}
