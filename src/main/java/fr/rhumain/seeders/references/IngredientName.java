package fr.rhumain.seeders.references;

public enum IngredientName {
    SAUCE_TOMATE("Sauce tomate"),
    CREME_FRAICHE("Crème fraiche"),
    OLIVE("Olive"),
    POIVRON("Poivron"),
    MOZZARELLA("Mozzarella"),
    JAMBON("Jambon"),
    CHAMPIGNON("Champignon"),
    ANANAS("Ananas"),
    OIGNON("Oignon"),
    THON("Thon"),
    BACON("Bacon"),
    CHORIZO("Chorizo"),
    POULET("Poulet"),
    STEAK_HACHE("Steak haché"),
    MERGUEZ("Merguez"),
    FROMAGE_CHEVRE("Fromage de chèvre"),
    ROQUEFORT("Roquefort"),
    GORGONZOLA("Gorgonzola"),
    PARMESAN("Parmesan");

    private final String name;

    IngredientName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
