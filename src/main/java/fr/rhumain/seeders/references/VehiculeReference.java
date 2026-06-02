package fr.rhumain.seeders.references;

import fr.rhumain.structs.Vehicule;

public enum VehiculeReference {
    SCOOTER("Peugeot", "Kisbee"),
    VELO("O2Feel", "iVog City"),
    VOITURE("Renault", "Twingo"),
    MOTO("Yamaha", "NMAX");

    private final String brand, model;
    VehiculeReference(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getModel() {
        return this.model;
    }
}
