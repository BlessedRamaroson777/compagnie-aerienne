package com.airline.airline_app.service;

import com.airline.airline_app.dto.ChiffreAffaireDTO;
import com.airline.airline_app.entity.Avion;
import com.airline.airline_app.entity.Vol;
import com.airline.airline_app.entity.VolProgramme;
import com.airline.airline_app.repository.AvionRepository;
import com.airline.airline_app.repository.ReservationRepository;
import com.airline.airline_app.repository.VolProgrammeRepository;
import com.airline.airline_app.repository.VolRepository;
import com.airline.airline_app.repository.DiffusionVolProgrammeRepository;
import com.airline.airline_app.repository.ConfigurationCoutDiffusionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChiffreAffaireService {

    private final ReservationRepository reservationRepository;
    private final VolProgrammeRepository volProgrammeRepository;
    private final AvionRepository avionRepository;
    private final VolRepository volRepository;
    private final DiffusionVolProgrammeRepository diffusionVolProgrammeRepository;
    private final ConfigurationCoutDiffusionRepository configurationCoutDiffusionRepository;

    /**
     * Obtenir le chiffre d'affaires global
     */
    public ChiffreAffaireDTO getCAGlobal() {
        BigDecimal total = reservationRepository.calculateTotalCA();
        Long nombreReservations = reservationRepository.count();
        BigDecimal totalRemises = reservationRepository.calculateTotalRemises();
        
        ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
        dto.setDesignation("Chiffre d'affaires global");
        dto.setType("GLOBAL");
        dto.setMontantTotal(total);
        dto.setNombreReservations(nombreReservations.intValue());
        dto.setMontantRemises(totalRemises);
        dto.setMontantAvantRemise(total.add(totalRemises));
        
        return dto;
    }

    /**
     * Obtenir le CA par vol programmé (avec date et heure)
     * Affiche TOUS les vols programmés, même ceux sans réservation
     */
    public List<ChiffreAffaireDTO> getCAParVolProgramme() {
        // Récupérer TOUS les vols programmés
        List<VolProgramme> tousLesVolsProgrammes = volProgrammeRepository.findAll();
        List<ChiffreAffaireDTO> dtos = new ArrayList<>();
        
        for (VolProgramme vp : tousLesVolsProgrammes) {
            Long volProgrammeId = vp.getId();
            
            // Calculer le montant des tickets vendus pour ce vol
            BigDecimal montantTickets = reservationRepository.calculateCAByVolProgramme(volProgrammeId);
            if (montantTickets == null) {
                montantTickets = BigDecimal.ZERO;
            }
            
            // Calculer le montant généré par la diffusion de publicité
            BigDecimal montantDiffusion = diffusionVolProgrammeRepository.calculateMontantDiffusionByVolProgramme(volProgrammeId);
            if (montantDiffusion == null) {
                montantDiffusion = BigDecimal.ZERO;
            }
            
            // Calculer le montant total à payer pour la diffusion
            BigDecimal montantDiffusionTotal = diffusionVolProgrammeRepository.calculateMontantDiffusionTotalByVolProgramme(volProgrammeId);
            if (montantDiffusionTotal == null) {
                montantDiffusionTotal = BigDecimal.ZERO;
            }
            
            // Calculer le montant déjà payé pour la diffusion
            BigDecimal montantDiffusionPaye = diffusionVolProgrammeRepository.calculateMontantDiffusionPayeByVolProgramme(volProgrammeId);
            if (montantDiffusionPaye == null) {
                montantDiffusionPaye = BigDecimal.ZERO;
            }
            
            // Calculer le reste à payer pour la diffusion
            BigDecimal montantDiffusionReste = diffusionVolProgrammeRepository.calculateMontantDiffusionResteByVolProgramme(volProgrammeId);
            if (montantDiffusionReste == null) {
                montantDiffusionReste = BigDecimal.ZERO;
            }
            
            // Compter les réservations
            Long nombreReservations = reservationRepository.countReservationsByVolProgramme(volProgrammeId);
            if (nombreReservations == null) {
                nombreReservations = 0L;
            }
            
            // Compter les billets
            Long nombreBillets = reservationRepository.countBilletsByVolProgramme(volProgrammeId);
            if (nombreBillets == null) {
                nombreBillets = 0L;
            }
            
            ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
            dto.setId(volProgrammeId);
            dto.setType("VOL_PROGRAMME");
            dto.setDesignation("Vol programmé: " + vp.getVol().getNumeroVol());
            dto.setNumeroVol(vp.getVol().getNumeroVol());
            dto.setMontantTickets(montantTickets);
            dto.setMontantDiffusion(montantDiffusion);
            dto.setMontantDiffusionTotal(montantDiffusionTotal);
            dto.setMontantDiffusionPaye(montantDiffusionPaye);
            dto.setMontantDiffusionReste(montantDiffusionReste);
            dto.setMontantTotal(montantTickets.add(montantDiffusion));
            dto.setNombreReservations(nombreReservations.intValue());
            dto.setNombreBillets(nombreBillets.intValue());
            dto.setDateHeureVol(vp.getDateHeureDepart());
            dto.setAeroportDepart(vp.getVol().getAeroportDepart().getNom());
            dto.setAeroportArrivee(vp.getVol().getAeroportArrivee().getNom());
            dto.setImmatriculation(vp.getAvion().getCodeAvion());
            
            dtos.add(dto);
        }
        
        // Trier par date/heure de départ décroissant (vols les plus récents en premier)
        dtos.sort((a, b) -> b.getDateHeureVol().compareTo(a.getDateHeureVol()));
        
        return dtos;
    }

    /**
     * Obtenir le CA pour un vol programmé spécifique
     */
    public ChiffreAffaireDTO getCAParVolProgramme(Long volProgrammeId) {
        VolProgramme vp = volProgrammeRepository.findById(volProgrammeId)
                .orElseThrow(() -> new RuntimeException("Vol programmé non trouvé"));
        
        BigDecimal montant = reservationRepository.calculateCAByVolProgramme(volProgrammeId);
        Long nombreBillets = reservationRepository.countBilletsByVolProgramme(volProgrammeId);
        BigDecimal remises = reservationRepository.calculateRemisesByVolProgramme(volProgrammeId);
        
        // Calculer le montant généré par la diffusion de publicité
        BigDecimal montantDiffusion = diffusionVolProgrammeRepository.calculateMontantDiffusionByVolProgramme(volProgrammeId);
        
        ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
        dto.setId(volProgrammeId);
        dto.setType("VOL_PROGRAMME");
        dto.setDesignation("Vol programmé: " + vp.getVol().getNumeroVol());
        dto.setNumeroVol(vp.getVol().getNumeroVol());
        
        // Montants détaillés
        dto.setMontantTickets(montant); // Montant des tickets vendus
        dto.setMontantDiffusion(montantDiffusion); // Montant de la diffusion
        dto.setMontantTotal(montant.add(montantDiffusion)); // Total = tickets + diffusion
        
        dto.setNombreBillets(nombreBillets.intValue());
        dto.setDateHeureVol(vp.getDateHeureDepart());
        dto.setAeroportDepart(vp.getVol().getAeroportDepart().getNom());
        dto.setAeroportArrivee(vp.getVol().getAeroportArrivee().getNom());
        dto.setImmatriculation(vp.getAvion().getCodeAvion());
        dto.setMontantRemises(remises);
        dto.setMontantAvantRemise(montant.add(remises));
        
        return dto;
    }

    /**
     * Obtenir le CA par avion
     */
    public List<ChiffreAffaireDTO> getCAParAvion() {
        List<Object[]> results = reservationRepository.calculateCAByAllAvions();
        List<ChiffreAffaireDTO> dtos = new ArrayList<>();
        
        for (Object[] result : results) {
            Long avionId = (Long) result[0];
            BigDecimal montant = (BigDecimal) result[1];
            Long nombreReservations = (Long) result[2];
            
            Avion avion = avionRepository.findById(avionId).orElse(null);
            if (avion != null) {
                ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
                dto.setId(avionId);
                dto.setType("AVION");
                dto.setDesignation("Avion: " + avion.getCodeAvion());
                dto.setImmatriculation(avion.getCodeAvion());
                dto.setMontantTotal(montant);
                dto.setNombreReservations(nombreReservations.intValue());
                
                dtos.add(dto);
            }
        }
        
        // Trier par montant décroissant
        dtos.sort((a, b) -> b.getMontantTotal().compareTo(a.getMontantTotal()));
        
        return dtos;
    }

    /**
     * Obtenir le CA pour un avion spécifique
     */
    public ChiffreAffaireDTO getCAParAvion(Long avionId) {
        Avion avion = avionRepository.findById(avionId)
                .orElseThrow(() -> new RuntimeException("Avion non trouvé"));
        
        BigDecimal montant = reservationRepository.calculateCAByAvion(avionId);
        
        ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
        dto.setId(avionId);
        dto.setType("AVION");
        dto.setDesignation("Avion: " + avion.getCodeAvion());
        dto.setImmatriculation(avion.getCodeAvion());
        dto.setMontantTotal(montant);
        
        return dto;
    }

    /**
     * Obtenir le CA par trajet (Vol de base)
     */
    public List<ChiffreAffaireDTO> getCAParVol() {
        List<Object[]> results = reservationRepository.calculateCAByAllVols();
        List<ChiffreAffaireDTO> dtos = new ArrayList<>();
        
        for (Object[] result : results) {
            Long volId = (Long) result[0];
            BigDecimal montant = (BigDecimal) result[1];
            Long nombreReservations = (Long) result[2];
            
            Vol vol = volRepository.findById(volId).orElse(null);
            if (vol != null) {
                ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
                dto.setId(volId);
                dto.setType("VOL");
                dto.setDesignation("Trajet: " + vol.getNumeroVol());
                dto.setNumeroVol(vol.getNumeroVol());
                dto.setMontantTotal(montant);
                dto.setNombreReservations(nombreReservations.intValue());
                dto.setAeroportDepart(vol.getAeroportDepart().getNom());
                dto.setAeroportArrivee(vol.getAeroportArrivee().getNom());
                
                dtos.add(dto);
            }
        }
        
        // Trier par montant décroissant
        dtos.sort((a, b) -> b.getMontantTotal().compareTo(a.getMontantTotal()));
        
        return dtos;
    }

    /**
     * Obtenir le CA pour un trajet (Vol) spécifique
     */
    public ChiffreAffaireDTO getCAParVol(Long volId) {
        Vol vol = volRepository.findById(volId)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        
        BigDecimal montant = reservationRepository.calculateCAByVol(volId);
        
        ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
        dto.setId(volId);
        dto.setType("VOL");
        dto.setDesignation("Trajet: " + vol.getNumeroVol());
        dto.setNumeroVol(vol.getNumeroVol());
        dto.setMontantTotal(montant);
        dto.setAeroportDepart(vol.getAeroportDepart().getNom());
        dto.setAeroportArrivee(vol.getAeroportArrivee().getNom());
        
        return dto;
    }
    
    /**
     * Obtenir le CA par catégorie de passagers
     */
    public List<ChiffreAffaireDTO> getCAParCategorie() {
        List<Object[]> results = reservationRepository.calculateCAByCategorie();
        List<ChiffreAffaireDTO> dtos = new ArrayList<>();
        
        for (Object[] result : results) {
            String categorieLibelle = (String) result[0];
            BigDecimal montant = (BigDecimal) result[1];
            Long nombrePassagers = (Long) result[2];
            
            ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
            dto.setType("CATEGORIE");
            dto.setDesignation("Catégorie: " + categorieLibelle);
            dto.setMontantTotal(montant);
            dto.setNombreBillets(nombrePassagers.intValue());
            
            dtos.add(dto);
        }
        
        // Trier par montant décroissant
        dtos.sort((a, b) -> b.getMontantTotal().compareTo(a.getMontantTotal()));
        
        return dtos;
    }
    
    /**
     * Obtenir le CA par catégorie pour un vol programmé
     */
    public List<ChiffreAffaireDTO> getCAParCategorieForVolProgramme(Long volProgrammeId) {
        List<Object[]> results = reservationRepository.calculateCAByCategorieForVolProgramme(volProgrammeId);
        List<ChiffreAffaireDTO> dtos = new ArrayList<>();
        
        for (Object[] result : results) {
            String categorieLibelle = (String) result[0];
            BigDecimal montant = (BigDecimal) result[1];
            Long nombrePassagers = (Long) result[2];
            
            ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
            dto.setType("CATEGORIE");
            dto.setDesignation("Catégorie: " + categorieLibelle);
            dto.setMontantTotal(montant);
            dto.setNombreBillets(nombrePassagers.intValue());
            
            dtos.add(dto);
        }
        
        // Trier par montant décroissant
        dtos.sort((a, b) -> b.getMontantTotal().compareTo(a.getMontantTotal()));
        
        return dtos;
    }
    
    /**
     * Obtenir le chiffre d'affaires des diffusions pour un mois et une année donnés
     * @param mois Le mois (1-12)
     * @param annee L'année (ex: 2026)
     * @return ChiffreAffaireDTO contenant les informations du CA des diffusions
     */
    public ChiffreAffaireDTO getChiffreAffaireDiffusion(int mois, int annee) {
        ChiffreAffaireDTO dto = new ChiffreAffaireDTO();
        dto.setType("DIFFUSION");
        dto.setDesignation(String.format("Chiffre d'affaires des diffusions - %02d/%d", mois, annee));
        
        // Récupérer toutes les diffusions de vol programme
        List<com.airline.airline_app.entity.DiffusionVolProgramme> diffusions = 
            diffusionVolProgrammeRepository.findAll();
        
        BigDecimal montantTotal = BigDecimal.ZERO;
        int nombreDiffusions = 0;
        
        for (com.airline.airline_app.entity.DiffusionVolProgramme diffusion : diffusions) {
            // Vérifier si la diffusion est dans le mois/année demandé
            if (diffusion.getDiffusionSociete() != null && 
                diffusion.getDiffusionSociete().getDate() != null) {
                
                int diffusionMois = diffusion.getDiffusionSociete().getDate().getMonthValue();
                int diffusionAnnee = diffusion.getDiffusionSociete().getDate().getYear();
                
                if (diffusionMois == mois && diffusionAnnee == annee) {
                    // Récupérer le prix de configuration pour cette diffusion
                    Long idDiffusionSociete = diffusion.getDiffusionSociete().getIdDiffusionSociete();
                    
                    // Calculer le coût de cette diffusion et l'ajouter au total
                    var configOpt = configurationCoutDiffusionRepository.findByDiffusionSocieteIdDiffusionSociete(idDiffusionSociete);
                    if (configOpt.isPresent()) {
                        BigDecimal montant = BigDecimal.valueOf(configOpt.get().getPrix())
                            .multiply(BigDecimal.valueOf(diffusion.getNombre()));
                        montantTotal = montantTotal.add(montant);
                    }
                    
                    nombreDiffusions++;
                }
            }
        }
        
        dto.setMontantTotal(montantTotal);
        dto.setNombreBillets(nombreDiffusions);
        
        return dto;
    }
}
