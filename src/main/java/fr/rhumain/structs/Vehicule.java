package fr.rhumain.structs;

public record Vehicule(Integer id, String brand, String model) {
    public static Vehicule of(String brand, String model) {
        return new Vehicule(null, brand, model);
    }
}
