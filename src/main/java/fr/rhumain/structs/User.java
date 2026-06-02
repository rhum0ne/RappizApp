package fr.rhumain.structs;

public record User(Integer id, String firstName, String lastName, String email, String password, Integer balance) {
    public static User of(String firstName, String lastName, String email, String password, Integer balance) {
        return new User(null, firstName, lastName, email, password, balance);
    }
}
