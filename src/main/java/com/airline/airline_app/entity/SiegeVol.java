package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "siege_vol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiegeVol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;
    
    @ManyToOne
    @JoinColumn(name = "siege_id", nullable = false)
    private Siege siege;
    
    @Column(nullable = false)
    private Boolean occupe = false;
    
    @OneToOne(mappedBy = "siegeVol")
    private Billet billet;
}
