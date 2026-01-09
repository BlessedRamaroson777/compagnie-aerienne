package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.RoleEquipage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEquipage role;
    
    @OneToMany(mappedBy = "equipage")
    private List<VolEquipage> vols = new ArrayList<>();
}
