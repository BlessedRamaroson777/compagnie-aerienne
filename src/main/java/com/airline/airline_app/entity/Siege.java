package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "siege", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"avion_id", "numero_siege"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Siege {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_siege", nullable = false, length = 10)
    private String numeroSiege;
    
    @ManyToOne
    @JoinColumn(name = "classe_siege_id", nullable = false)
    private ClasseSiege classeSiege;
    
    @ManyToOne
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "siege")
    private List<SiegeVol> siegesVol = new ArrayList<>();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
