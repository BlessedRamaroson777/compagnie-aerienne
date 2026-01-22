package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "societe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Societe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_societe")
    private Long idSociete;

    @Column(nullable = false, length = 100)
    private String libelle;

    @OneToMany(mappedBy = "societe", cascade = CascadeType.ALL)
    private List<DiffusionSociete> diffusions = new ArrayList<>();
}
