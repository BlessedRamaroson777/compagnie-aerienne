package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aeroport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aeroport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code_aeroport", nullable = false, unique = true, length = 10)
    private String codeAeroport;
    
    @Column(nullable = false, length = 150)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String ville;
    
    @Column(nullable = false, length = 100)
    private String pays;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "aeroportDepart")
    private List<Vol> volsDepart;
    
    @OneToMany(mappedBy = "aeroportArrivee")
    private List<Vol> volsArrivee;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
