package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.StatusHistorique;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_configuration_cout")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueConfigurationCout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique_configuration_cout")
    private Long idHistoriqueConfigurationCout;

    @ManyToOne
    @JoinColumn(name = "id_configuration_cout_diffusion")
    private ConfigurationCoutDiffusion configurationCoutDiffusion;

    @Column(nullable = false)
    private Double prix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusHistorique status;

    @Column(nullable = false)
    private LocalDateTime date;
}
