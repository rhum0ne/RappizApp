package fr.rhumain.structs;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public record Order(Integer id, User User, Pizza pizza, Format format, Timestamp timeStamp, Timestamp timeStampLivraison, int price, Livreur livreur, Vehicule vehicule) {
}
