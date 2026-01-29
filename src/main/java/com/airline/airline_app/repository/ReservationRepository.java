package com.airline.airline_app.repository;

import com.airline.airline_app.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    
    // Chiffre d'affaires par vol programmé
    @Query("SELECT COALESCE(SUM(r.prixTotal), 0) FROM Reservation r WHERE r.volProgramme.id = :volProgrammeId")
    BigDecimal calculateCAByVolProgramme(@Param("volProgrammeId") Long volProgrammeId);
    
    @Query("SELECT r.volProgramme.id, COALESCE(SUM(r.prixTotal), 0), COUNT(r) " +
           "FROM Reservation r " +
           "GROUP BY r.volProgramme.id")
    List<Object[]> calculateCAByAllVolProgrammes();
    
    // Chiffre d'affaires par avion
    @Query("SELECT COALESCE(SUM(r.prixTotal), 0) FROM Reservation r WHERE r.volProgramme.avion.id = :avionId")
    BigDecimal calculateCAByAvion(@Param("avionId") Long avionId);
    
    @Query("SELECT r.volProgramme.avion.id, COALESCE(SUM(r.prixTotal), 0), COUNT(r) " +
           "FROM Reservation r " +
           "GROUP BY r.volProgramme.avion.id")
    List<Object[]> calculateCAByAllAvions();
    
    // Chiffre d'affaires par trajet (Vol)
    @Query("SELECT COALESCE(SUM(r.prixTotal), 0) FROM Reservation r WHERE r.volProgramme.vol.id = :volId")
    BigDecimal calculateCAByVol(@Param("volId") Long volId);
    
    @Query("SELECT r.volProgramme.vol.id, COALESCE(SUM(r.prixTotal), 0), COUNT(r) " +
           "FROM Reservation r " +
           "GROUP BY r.volProgramme.vol.id")
    List<Object[]> calculateCAByAllVols();
    
    // Nombre de billets par vol programmé
    @Query("SELECT COUNT(b) FROM Billet b WHERE b.reservation.volProgramme.id = :volProgrammeId")
    Long countBilletsByVolProgramme(@Param("volProgrammeId") Long volProgrammeId);
    
    // Nombre de réservations par vol programmé
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.volProgramme.id = :volProgrammeId")
    Long countReservationsByVolProgramme(@Param("volProgrammeId") Long volProgrammeId);
    
    // Chiffre d'affaires global
    @Query("SELECT COALESCE(SUM(r.prixTotal), 0) FROM Reservation r")
    BigDecimal calculateTotalCA();
    
    // Chiffre d'affaires par catégorie de passagers
    @Query("SELECT rcp.categoriePassager.libelle, COALESCE(SUM(rcp.sousTotal), 0), SUM(rcp.nombrePassagers) " +
           "FROM ReservationCategoriePassager rcp " +
           "GROUP BY rcp.categoriePassager.id, rcp.categoriePassager.libelle")
    List<Object[]> calculateCAByCategorie();
    
    // Chiffre d'affaires par catégorie pour un vol programmé
    @Query("SELECT rcp.categoriePassager.libelle, COALESCE(SUM(rcp.sousTotal), 0), SUM(rcp.nombrePassagers) " +
           "FROM ReservationCategoriePassager rcp " +
           "WHERE rcp.reservation.volProgramme.id = :volProgrammeId " +
           "GROUP BY rcp.categoriePassager.id, rcp.categoriePassager.libelle")
    List<Object[]> calculateCAByCategorieForVolProgramme(@Param("volProgrammeId") Long volProgrammeId);
    
    // Total des remises accordées
    @Query("SELECT COALESCE(SUM(rcp.remiseUnitaire * rcp.nombrePassagers), 0) " +
           "FROM ReservationCategoriePassager rcp")
    BigDecimal calculateTotalRemises();
    
    // Remises par vol programmé
    @Query("SELECT COALESCE(SUM(rcp.remiseUnitaire * rcp.nombrePassagers), 0) " +
           "FROM ReservationCategoriePassager rcp " +
           "WHERE rcp.reservation.volProgramme.id = :volProgrammeId")
    BigDecimal calculateRemisesByVolProgramme(@Param("volProgrammeId") Long volProgrammeId);
    
    // Réservations par vol programmé (pour suivi diffusion)
    @Query("SELECT r FROM Reservation r WHERE r.volProgramme.id = :idVolProgramme")
    List<Reservation> findByVolProgrammeIdVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
}
