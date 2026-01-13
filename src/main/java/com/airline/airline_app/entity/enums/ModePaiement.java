package com.airline.airline_app.entity.enums;

public enum ModePaiement {
    ESPECES("Espèces"),
    CARTE_BANCAIRE("Carte Bancaire"),
    VIREMENT("Virement Bancaire"),
    MOBILE_MONEY("Mobile Money"),
    CHEQUE("Chèque");

    private final String libelle;

    ModePaiement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
