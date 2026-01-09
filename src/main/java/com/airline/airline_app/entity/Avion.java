package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    @Column(unique = true, nullable = false)
    private String codeAvion;
    
    @Column(nullable = false)
    private String modele;
    
    @Column(nullable = false)
    private Integer capaciteTotale;
    
    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Siege> sieges = new ArrayList<>();
    
    @OneToMany(mappedBy = "avion")
    private List<Vol> vols = new ArrayList<>();
}
