package com.airline.airline_app.service;

import com.airline.airline_app.entity.VenteProduit;
import com.airline.airline_app.repository.VenteProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VenteProduitService {

    private final VenteProduitRepository venteProduitRepository;

    public List<VenteProduit> findAll() {
        return venteProduitRepository.findAll();
    }

    public VenteProduit findById(Long id) {
        return venteProduitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));
    }

    @Transactional
    public VenteProduit save(VenteProduit vente) {
        // Le trigger se chargera de décrémenter le stock et d'insérer dans l'historique
        return venteProduitRepository.save(vente);
    }

    @Transactional
    public void delete(Long id) {
        venteProduitRepository.deleteById(id);
    }

    public List<VenteProduit> findByVolProgramme(Long idVolProgramme) {
        return venteProduitRepository.findByVolProgrammeId(idVolProgramme);
    }

    public List<VenteProduit> findByProduit(Long idProduit) {
        return venteProduitRepository.findByProduitIdProduit(idProduit);
    }

    public BigDecimal calculateCAVenteByVolProgramme(Long idVolProgramme) {
        return venteProduitRepository.calculateCAVenteByVolProgramme(idVolProgramme);
    }

    public BigDecimal calculateCAVenteByMois(Integer mois, Integer annee) {
        return venteProduitRepository.calculateCAVenteByMois(mois, annee);
    }
}
