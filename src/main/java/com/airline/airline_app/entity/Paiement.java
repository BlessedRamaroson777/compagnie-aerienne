package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.StatutPaiement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paiement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @Column(nullable = false)
    private BigDecimal montant;
    
    @Column(nullable = false)
    private LocalDateTime datePaiement;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;
    
    @OneToMany(mappedBy = "paiement", cascade = CascadeType.ALL)
    private List<PaiementMethode> methodesUtilisees = new ArrayList<>();
}
