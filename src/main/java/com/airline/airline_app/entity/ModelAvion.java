package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "model_avion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelAvion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String designation;
    
    @Column(nullable = false, length = 100)
    private String fabricant;
    
    @Column(nullable = false)
    private Integer capacite;
    
    @Column(name = "autonomie_km")
    private Integer autonomieKm;
    
    @Column(name = "vitesse_km_h")
    private Integer vitesseKmH;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "modelAvion")
    private List<Avion> avions;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
