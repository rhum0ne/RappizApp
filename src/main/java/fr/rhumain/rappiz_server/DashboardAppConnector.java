package fr.rhumain.rappiz_server;

import fr.rhumain.dao.LivreurDAO;
import fr.rhumain.dao.OrderDAO;
import fr.rhumain.dao.ReceiptDAO;
import fr.rhumain.dao.ReportDAO;
import fr.rhumain.dao.UserDAO;
import fr.rhumain.dao.VehiculeDAO;
import fr.rhumain.exceptions.DAOException;
import fr.rhumain.structs.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DashboardAppConnector {

    private final OrderDAO orderDAO;
    private final UserDAO userDAO;
    private final VehiculeDAO vehiculeDAO;
    private final LivreurDAO livreurDAO;
    private final ReportDAO reportDAO;
    private final ReceiptDAO receiptDAO;

    public DashboardAppConnector() {
        this(new OrderDAO(), new UserDAO(), new VehiculeDAO(), new LivreurDAO(), new ReportDAO(), new ReceiptDAO());
    }

    public DashboardAppConnector(OrderDAO orderDAO, UserDAO userDAO, VehiculeDAO vehiculeDAO, LivreurDAO livreurDAO, ReportDAO reportDAO, ReceiptDAO receiptDAO) {
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.vehiculeDAO = vehiculeDAO;
        this.livreurDAO = livreurDAO;
        this.reportDAO = reportDAO;
        this.receiptDAO = receiptDAO;
    }

    /**
     * Récupère toutes les commandes de la plateforme pour le dashboard.
     */
    public List<Order> getAllOrders() {
        try {
            return orderDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    public List<Vehicule> getAllVehicules() {
        try {
            return vehiculeDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    public List<Livreur> getAllLivreurs() {
        try {
            return livreurDAO.findAll();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    public List<Vehicule> getUnusedVehicules() {
        try {
            return reportDAO.findUnusedVehicles();
        } catch (DAOException e) {
            logDaoError(e);
            return Collections.emptyList();
        }
    }

    public User getUserById(int idUser) {
        try {
            return userDAO.findById(idUser).orElse(null);
        } catch (DAOException e) {
            logDaoError(e);
            return null;
        }
    }

    public boolean markOrderDelivered(int idOrder, Integer idLivreur, Integer idVehicule) {
        if (idLivreur == null || idVehicule == null) {
            return false;
        }

        try {
            Order order = orderDAO.findById(idOrder).orElse(null);
            if (order == null || order.timeStampLivraison() != null) {
                return false;
            }

            Livreur livreur = livreurDAO.findById(idLivreur).orElse(null);
            Vehicule vehicule = vehiculeDAO.findById(idVehicule).orElse(null);
            if (livreur == null || vehicule == null) {
                return false;
            }

            LocalDateTime deliveredAt = LocalDateTime.now();
            int finalPrice = isLateDelivery(order, deliveredAt) ? 0 : order.price();
            orderDAO.update(new Order(
                    order.id(),
                    order.User(),
                    order.pizza(),
                    order.format(),
                    order.timeStamp(),
                    deliveredAt,
                    finalPrice,
                    livreur,
                    vehicule
            ));
            if (finalPrice == 0 && order.price() > 0) {
                refundLateOrder(order);
            }
            return true;
        } catch (DAOException e) {
            logDaoError(e);
            return false;
        }
    }

    private boolean isLateDelivery(Order order, LocalDateTime deliveredAt) {
        return Duration.between(order.timeStamp(), deliveredAt).toMinutes() > 30;
    }

    private void refundLateOrder(Order order) throws DAOException {
        User user = order.User();
        if (user != null) {
            userDAO.updateBalance(user.id(), user.balance() + order.price());
        }
        Optional<Receipt> receipt = receiptDAO.findByOrder(order.id());
        if (receipt.isPresent()) {
            Receipt existingReceipt = receipt.get();
            receiptDAO.update(new Receipt(existingReceipt.id(), existingReceipt.idOrder(), 0, existingReceipt.idUser()));
        }
    }

    private void logDaoError(DAOException e) {
        System.err.println("[DashboardAppConnector] " + e.getMessage());
    }
}