package fr.rhumain.structs;

import fr.rhumain.seeders.references.IngredientName;

public record Ingredient(Integer id, String nom) {
    public static Ingredient of(String nom) {
        return new Ingredient(null, nom);
    }
}
