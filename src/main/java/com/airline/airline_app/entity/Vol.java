package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_vol", nullable = false, length = 50)
    private String numeroVol;

    @ManyToOne
    @JoinColumn(name = "aeroport_depart_id", nullable = false)
    private Aeroport aeroportDepart;

    @ManyToOne
    @JoinColumn(name = "aeroport_arrivee_id", nullable = false)
    private Aeroport aeroportArrivee;
    
    @Column(name = "duree_vol_minutes")
    private Integer dureeVolMinutes; // Durée du vol en minutes

    @ToString.Exclude
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<VolProgramme> volsProgrammes = new ArrayList<>();
    
    /**
     * Retourne la durée formatée (ex: "2h 30min")
     */
    public String getDureeFormatee() {
        if (dureeVolMinutes == null || dureeVolMinutes == 0) return "N/A";
        int heures = dureeVolMinutes / 60;
        int minutes = dureeVolMinutes % 60;
        if (heures > 0 && minutes > 0) {
            return heures + "h " + minutes + "min";
        } else if (heures > 0) {
            return heures + "h";
        } else {
            return minutes + "min";
        }
    }
}
