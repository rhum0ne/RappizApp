package fr.rhumain.structs;

import java.time.LocalDateTime;

public record Order(Integer id, User User, Pizza pizza, Format format, LocalDateTime timeStamp, LocalDateTime timeStampLivraison, int price, Livreur livreur, Vehicule vehicule) {
}
