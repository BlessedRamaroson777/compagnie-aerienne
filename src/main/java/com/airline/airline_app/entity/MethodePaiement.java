package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.TypeMethodePaiement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "methode_paiement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodePaiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TypeMethodePaiement libelle;
    
    @OneToMany(mappedBy = "methodePaiement")
    private List<PaiementMethode> paiementsMethodes = new ArrayList<>();
}
