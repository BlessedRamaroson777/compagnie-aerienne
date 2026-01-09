package com.airline.airline_app.entity;

import com.airline.airline_app.entity.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;
    
    @Column(nullable = false)
    private LocalDateTime dateReservation;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut;
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Billet> billets = new ArrayList<>();
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Paiement> paiements = new ArrayList<>();
}
