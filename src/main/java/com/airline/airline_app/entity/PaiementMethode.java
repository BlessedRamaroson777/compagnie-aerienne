package com.airline.airline_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "paiement_methode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementMethode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;
    
    @ManyToOne
    @JoinColumn(name = "methode_paiement_id", nullable = false)
    private MethodePaiement methodePaiement;
    
    @Column(nullable = false)
    private BigDecimal montant;
}
