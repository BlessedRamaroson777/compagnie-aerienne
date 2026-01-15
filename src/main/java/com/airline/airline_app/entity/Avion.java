package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code_avion", unique = true, nullable = false, length = 50)
    private String codeAvion;
    
    @ManyToOne
    @JoinColumn(name = "model_avion_id", nullable = false)
    private ModelAvion modelAvion;
    
    @ManyToOne
    @JoinColumn(name = "etat_avion_id", nullable = false)
    private EtatAvion etatAvion;
    
    @Column(name = "capacite_totale", nullable = false)
    private Integer capaciteTotale;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Siege> sieges = new ArrayList<>();
    
    @OneToMany(mappedBy = "avion")
    private List<VolProgramme> volsProgrammes = new ArrayList<>();
    
    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvionClasseConfiguration> configurations = new ArrayList<>();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
