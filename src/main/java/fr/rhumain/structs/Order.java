package fr.rhumain.structs;

public record Order(int id, int idUser, Pizza pizza, Format format, String timeStamp, String timeStampLivraison, int price, Livreur livreur, Vehicule vehicule) {
}
