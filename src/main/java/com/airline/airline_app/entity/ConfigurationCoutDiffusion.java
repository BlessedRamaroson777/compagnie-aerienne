package com.airline.airline_app.entity;

import com.airline.airline_app.listener.ConfigurationCoutDiffusionListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "configuration_cout_diffusion")
@EntityListeners(ConfigurationCoutDiffusionListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationCoutDiffusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuration_cout_diffusion")
    private Long idConfigurationCoutDiffusion;

    @ManyToOne
    @JoinColumn(name = "id_diffusion_societe", nullable = false)
    private DiffusionSociete diffusionSociete;

    @Column(nullable = false)
    private Double prix;

    @ToString.Exclude
    @OneToMany(mappedBy = "configurationCoutDiffusion", cascade = CascadeType.ALL)
    private List<HistoriqueConfigurationCout> historiques = new ArrayList<>();
}
