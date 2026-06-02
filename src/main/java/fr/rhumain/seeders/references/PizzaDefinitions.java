package fr.rhumain.seeders.references;

import java.util.List;
import java.util.Set;

import static fr.rhumain.seeders.references.IngredientName.*;

/**
 * Class to define the pizzas
 */
public final class PizzaDefinitions {
    private PizzaDefinitions() {}

    public static final List<PizzaDefinition> ALL = List.of(

            new PizzaDefinition("Margherita", 900,
                    Set.of(SAUCE_TOMATE, MOZZARELLA)),

            new PizzaDefinition("Reine", 1100,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, JAMBON, CHAMPIGNON)),

            new PizzaDefinition("4 Fromages", 1250,
                    Set.of(CREME_FRAICHE, MOZZARELLA, ROQUEFORT, GORGONZOLA, PARMESAN)),

            new PizzaDefinition("Hawaïenne", 1150,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, JAMBON, ANANAS)),

            new PizzaDefinition("Chèvre Miel", 1200,
                    Set.of(CREME_FRAICHE, MOZZARELLA, FROMAGE_CHEVRE, OIGNON)),

            new PizzaDefinition("Mexicaine", 1300,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, STEAK_HACHE, MERGUEZ, POIVRON, OIGNON)),

            new PizzaDefinition("Campione", 1250,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, CHORIZO, POIVRON, OIGNON, OLIVE)),

            new PizzaDefinition("Océane", 1150,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, THON, OIGNON, OLIVE)),

            new PizzaDefinition("BBQ Chicken", 1300,
                    Set.of(SAUCE_TOMATE, MOZZARELLA, POULET, BACON, OIGNON))
    );
}
