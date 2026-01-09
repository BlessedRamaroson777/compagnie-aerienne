package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.ClasseSiege;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "siege")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Siege {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String numeroSiege;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClasseSiege classeSiege;
    
    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;
    
    @OneToMany(mappedBy = "siege")
    private List<SiegeVol> siegesVol = new ArrayList<>();
}
