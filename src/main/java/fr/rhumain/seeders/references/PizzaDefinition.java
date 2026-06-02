package fr.rhumain.seeders.references;

import java.util.Set;

/**
 * Record used to seed pizzas in database only
 */
public record PizzaDefinition(String name, int price, Set<IngredientName> ingredients) {
}
