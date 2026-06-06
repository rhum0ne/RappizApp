package fr.rhumain.rappiz_server;

import fr.rhumain.dao.FormatDAO;
import fr.rhumain.dao.OrderDAO;
import fr.rhumain.dao.PizzaDAO;
import fr.rhumain.dao.ReceiptDAO;
import fr.rhumain.dao.UserDAO;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ClientAppConnector {

    private final PizzaDAO pizzaDAO;
    private final FormatDAO formatDAO;
    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final ReceiptDAO receiptDAO;

    public ClientAppConnector() {
        this(new PizzaDAO(), new FormatDAO(), new UserDAO(), new OrderDAO(), new ReceiptDAO());
    }

    public ClientAppConnector(PizzaDAO pizzaDAO, FormatDAO formatDAO, UserDAO userDAO, OrderDAO orderDAO, ReceiptDAO receiptDAO) {
        this.pizzaDAO = pizzaDAO;
        this.formatDAO = formatDAO;
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        this.receiptDAO = receiptDAO;
    }

    /**
     * Récupère la liste des pizzas disponibles au menu.
     */
    public List<Pizza> getPizzas() {
        try {
            return pizzaDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Récupère les formats disponibles pour composer une commande.
     */
    public List<Format> getFormats() {
        try {
            return formatDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Simule la création d'une commande depuis l'application cliente.
     */
    public boolean createOrder(int idUser, Pizza pizza, Format format) {
        try {
            User user = userDAO.findById(idUser).orElse(null);
            if (user == null) {
                return false;
            }

            int loyaltyPizzaCount = getBoughtPizzaCount(user.id());
            boolean loyaltyFreePizza = loyaltyPizzaCount > 0 && (loyaltyPizzaCount + 1) % 10 == 0;
            int price = loyaltyFreePizza ? 0 : calculatePrice(pizza, format);
            if (user.balance() < price) {
                System.out.println("Commande refusée pour le client #" + idUser + " : solde insuffisant");
                return false;
            }

            Order order = orderDAO.save(new Order(
                    null,
                    user,
                    pizza,
                    format,
                    LocalDateTime.now(),
                    null,
                    price,
                    null,
                    null
            ));
            receiptDAO.save(new Receipt(null, order.id(), order.price(), user.id()));
            userDAO.updateBalance(user.id(), user.balance() - price);
            System.out.println("Nouvelle commande #" + order.id() + " créée pour le client #" + idUser);
            return true;
        } catch (DAOException e) {
            logDaoError(e);
            System.out.println("Commande refusée pour le client #" + idUser + " : solde insuffisant");
            return false;
        }
    }

    public int calculatePrice(Pizza pizza, Format format) {
        return (pizza.price() * format.pricePercetage()) / 100;
    }

    public int getCustomerBalance(int idUser) {
        User user = getUserById(idUser);
        return user != null ? user.balance() : 0;
    }

    public int getBoughtPizzaCount(int idUser) {
        // Le cycle fidélité doit avancer même quand la pizza courante est offerte,
        // sinon le client reste bloqué à 9/10 et obtient des pizzas gratuites à l'infini.
        return (int) getOrdersByUserId(idUser).stream()
                .count();
    }

    public Optional<User> authenticateUser(String email, String password) {
        try {
            return userDAO.findByEmail(email)
                    .filter(user -> user.password().equals(password));
        } catch (DAOException e) {
            logDaoError(e);
            return Optional.empty();
        }
    }

    /**
     * Récupère les informations basiques du client depuis son ID.
     */
    public User getUserById(int idUser) {
        try {
            return userDAO.findById(idUser).orElse(null);
        } catch (DAOException e) {
            logDaoError(e);
            return null;
        }
    }

    /**
     * Récupère l'historique ou les commandes en cours d'un client.
     */
    public List<Order> getOrdersByUserId(int idUser) {
        try {
            return orderDAO.findByUser(idUser)
                    .stream()
                    .sorted(Comparator.comparingInt(Order::id).reversed())
                    .toList();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Récupère la liste des reçus / factures associés à l'utilisateur.
     */
    public List<Receipt> getReceiptsByUserId(int idUser) {
        try {
            return receiptDAO.findByUser(idUser);
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    /**
     * Méthode bonus : si tu as besoin de simuler l'état d'une commande en direct.
     */
    public Order getActiveOrder(int idUser) {
        return getOrdersByUserId(idUser)
                .stream()
                .filter(order -> order.timeStampLivraison() == null)
                .findFirst()
                .orElse(null);
    }

    private void logDaoError(DAOException e) {
        System.err.println("[ClientAppConnector] " + e.getMessage());
    }
}