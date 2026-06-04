DROP database IF EXISTS rapizz;

CREATE database IF NOT EXISTS rapizz;

USE rapizz;

CREATE TABLE pizzas(
                       id INT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL,
                       price INT NOT NULL,
                       PRIMARY KEY(id)
);

CREATE TABLE ingredients(
                            id INT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(50) NOT NULL,
                            PRIMARY KEY(id)
);

CREATE TABLE formats(
                      id INT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(50) NOT NULL,
                      percentage INT NOT NULL,
                      PRIMARY KEY(id)
);

CREATE TABLE users(
                          id INT NOT NULL AUTO_INCREMENT,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          email VARCHAR(50) NOT NULL,
                          password VARCHAR(50) NOT NULL,
                          balance INT NOT NULL,
                          PRIMARY KEY(id)
);

CREATE TABLE receipts(
                         id INT NOT NULL AUTO_INCREMENT,
                         id_order INT NOT NULL,
                         id_user INT NOT NULL,
                         final_price INT NOT NULL,
                         PRIMARY KEY(id)
);

CREATE TABLE orders(
                       id INT NOT NULL AUTO_INCREMENT,
                       id_user INT NOT NULL,
                       id_pizza INT NOT NULL,
                       id_format INT NOT NULL,
                       timestamp_order DATETIME NOT NULL,
                       timestamp_deliver DATETIME NULL,
                       final_price INT NOT NULL,
                       id_deliver INT NULL,
                       id_vehicule INT NULL,
                       PRIMARY KEY(id)
);

CREATE TABLE delivers(
                         id INT NOT NULL AUTO_INCREMENT,
                         first_name VARCHAR(50) NOT NULL,
                         last_name VARCHAR(50) NOT NULL,
                         email VARCHAR(50) NOT NULL,
                         password VARCHAR(50) NOT NULL,
                         PRIMARY KEY(id)
);

CREATE TABLE vehicules(
                          id INT NOT NULL AUTO_INCREMENT,
                          brand VARCHAR(50) NOT NULL,
                          model VARCHAR(50) NOT NULL,
                          PRIMARY KEY(id)
);

CREATE TABLE pizza_ingredients(
                                  id_pizza_ingredients INT NOT NULL AUTO_INCREMENT,
                                  id_pizza INT NOT NULL,
                                  id_ingredient INT NOT NULL,
                                  PRIMARY KEY(id_pizza_ingredients)
);


-- CONSTRAINTS

ALTER TABLE pizza_ingredients
    ADD CONSTRAINT fk_pizzas_pizza_ingredients
        FOREIGN KEY (id_pizza)
            REFERENCES pizzas(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;

ALTER TABLE pizza_ingredients
    ADD CONSTRAINT fk_ingredients_pizza_ingredients
        FOREIGN KEY (id_ingredient)
            REFERENCES ingredients(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE orders
    ADD CONSTRAINT fk_pizzas_orders
        FOREIGN KEY (id_pizza)
            REFERENCES pizzas(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE orders
    ADD CONSTRAINT fk_customers_orders
        FOREIGN KEY (id_user)
            REFERENCES users(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE orders
    ADD CONSTRAINT fk_delivers_orders
        FOREIGN KEY (id_deliver)
            REFERENCES delivers(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE orders
    ADD CONSTRAINT fk_vehicules_orders
        FOREIGN KEY (id_vehicule)
            REFERENCES vehicules(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE orders
    ADD CONSTRAINT fk_sizes_orders
        FOREIGN KEY (id_format)
            REFERENCES formats(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE receipts
    ADD CONSTRAINT fk_orders_receipts
        FOREIGN KEY (id_order)
            REFERENCES orders(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;;

ALTER TABLE receipts
    ADD CONSTRAINT fk_customers_receipts
        FOREIGN KEY (id_user)
            REFERENCES users(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;


-- STORED PROCEDURES

DELIMITER $$

CREATE PROCEDURE sp_menu()
BEGIN
    SELECT
        p.name AS pizza_name,
        p.price AS base_price,
        GROUP_CONCAT(i.name ORDER BY i.name SEPARATOR ', ') AS ingredients
    FROM pizzas p
             LEFT JOIN pizza_ingredients pi ON p.id = pi.id_pizza
             LEFT JOIN ingredients i ON pi.id_ingredient = i.id
    GROUP BY p.id, p.name, p.price
    ORDER BY p.name;
END$$

CREATE PROCEDURE sp_delivery_sheet(IN p_order_id INT)
BEGIN
    SELECT
        CONCAT(d.first_name, ' ', d.last_name) AS deliver_name,
        CASE
            WHEN LOWER(CONCAT(v.brand, ' ', v.model)) LIKE '%kisbee%'
                OR LOWER(CONCAT(v.brand, ' ', v.model)) LIKE '%nmax%' THEN 'Moto'
            WHEN LOWER(CONCAT(v.brand, ' ', v.model)) LIKE '%o2feel%' THEN 'Vélo'
            ELSE 'Voiture'
            END AS vehicle_type,
        CONCAT(u.first_name, ' ', u.last_name) AS client_name,
        o.timestamp_order,
        GREATEST(TIMESTAMPDIFF(MINUTE, o.timestamp_order, o.timestamp_deliver) - 30, 0) AS delay_minutes,
        p.name AS pizza_name,
        p.price AS base_price
    FROM orders o
             JOIN users u ON o.id_user = u.id
             JOIN pizzas p ON o.id_pizza = p.id
             LEFT JOIN delivers d ON o.id_deliver = d.id
             LEFT JOIN vehicules v ON o.id_vehicule = v.id
    WHERE o.id = p_order_id;
END$$

CREATE PROCEDURE sp_orders_by_client()
BEGIN
    SELECT
        u.id,
        CONCAT(u.first_name, ' ', u.last_name) AS client_name,
        COUNT(o.id) AS order_count
    FROM users u
             LEFT JOIN orders o ON u.id = o.id_user
    GROUP BY u.id, u.first_name, u.last_name
    ORDER BY order_count DESC;
END$$

CREATE PROCEDURE sp_average_orders()
BEGIN
    SELECT AVG(order_count) AS average_orders
    FROM (
             SELECT COUNT(o.id) AS order_count
             FROM users u
                      LEFT JOIN orders o ON u.id = o.id_user
             GROUP BY u.id
         ) client_orders;
END$$

CREATE PROCEDURE sp_clients_above_average()
BEGIN
    SELECT client_name, order_count
    FROM (
             SELECT
                 u.id,
                 CONCAT(u.first_name, ' ', u.last_name) AS client_name,
                 COUNT(o.id) AS order_count
             FROM users u
                      LEFT JOIN orders o ON u.id = o.id_user
             GROUP BY u.id, u.first_name, u.last_name
         ) client_orders
    WHERE order_count > (
        SELECT AVG(order_count)
        FROM (
                 SELECT COUNT(o.id) AS order_count
                 FROM users u
                          LEFT JOIN orders o ON u.id = o.id_user
                 GROUP BY u.id
             ) averages
    );
END$$

CREATE PROCEDURE sp_unused_vehicles()
BEGIN
    SELECT v.id, v.brand, v.model
    FROM vehicules v
             LEFT JOIN orders o ON v.id = o.id_vehicule
    WHERE o.id IS NULL;
END$$

CREATE TRIGGER trg_orders_require_delivery_assignment_insert
    BEFORE INSERT ON orders
    FOR EACH ROW
BEGIN
    IF NEW.timestamp_deliver IS NOT NULL
        AND (NEW.id_deliver IS NULL OR NEW.id_vehicule IS NULL) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A delivered order requires both a deliver and a vehicle';
    END IF;
END$$

CREATE TRIGGER trg_orders_require_delivery_assignment_update
    BEFORE UPDATE ON orders
    FOR EACH ROW
BEGIN
    IF NEW.timestamp_deliver IS NOT NULL
        AND (NEW.id_deliver IS NULL OR NEW.id_vehicule IS NULL) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A delivered order requires both a deliver and a vehicle';
    END IF;
END$$

DELIMITER ;


-- INSERTION DATA

INSERT INTO formats (id, name, percentage) VALUES
    (1, 'Naine', 67),
    (2, 'Humaine', 100),
    (3, 'Ogresse', 134);

INSERT INTO ingredients (id, name) VALUES
    (1, 'Sauce tomate'),
    (2, 'Crème fraiche'),
    (3, 'Olive'),
    (4, 'Poivron'),
    (5, 'Mozzarella'),
    (6, 'Jambon'),
    (7, 'Champignon'),
    (8, 'Ananas'),
    (9, 'Oignon'),
    (10, 'Thon'),
    (11, 'Bacon'),
    (12, 'Chorizo'),
    (13, 'Poulet'),
    (14, 'Steak haché'),
    (15, 'Merguez'),
    (16, 'Fromage de chèvre'),
    (17, 'Roquefort'),
    (18, 'Gorgonzola'),
    (19, 'Parmesan');

INSERT INTO pizzas (id, name, price) VALUES
    (1, 'Margherita', 900),
    (2, 'Reine', 1100),
    (3, '4 Fromages', 1250),
    (4, 'Hawaïenne', 1150),
    (5, 'Chèvre Miel', 1200),
    (6, 'Mexicaine', 1300),
    (7, 'Campione', 1250),
    (8, 'Océane', 1150),
    (9, 'BBQ Chicken', 1300);

INSERT INTO pizza_ingredients (id_pizza, id_ingredient) VALUES
    (1, 1), (1, 5),
    (2, 1), (2, 5), (2, 6), (2, 7),
    (3, 2), (3, 5), (3, 17), (3, 18), (3, 19),
    (4, 1), (4, 5), (4, 6), (4, 8),
    (5, 2), (5, 5), (5, 16), (5, 9),
    (6, 1), (6, 5), (6, 14), (6, 15), (6, 4), (6, 9),
    (7, 1), (7, 5), (7, 12), (7, 4), (7, 9), (7, 3),
    (8, 1), (8, 5), (8, 10), (8, 9), (8, 3),
    (9, 1), (9, 5), (9, 13), (9, 11), (9, 9);

INSERT INTO users (id, first_name, last_name, email, password, balance) VALUES
    (1, 'Jean', 'Dupont', 'jean.dupont@rappiz.fr', 'rappiz', 5000),
    (2, 'Sarah', 'Martin', 'sarah.martin@rappiz.fr', 'rappiz', 2750),
    (3, 'Yanis', 'Petit', 'yanis.petit@rappiz.fr', 'rappiz', 230);

INSERT INTO delivers (id, first_name, last_name, email, password) VALUES
    (1, 'Marc', 'Lefèvre', 'marc.lefevre@rappiz.fr', 'rappiz'),
    (2, 'Nora', 'Bailly', 'nora.bailly@rappiz.fr', 'rappiz'),
    (3, 'Hugo', 'Bernard', 'hugo.bernard@rappiz.fr', 'rappiz');

INSERT INTO vehicules (id, brand, model) VALUES
    (1, 'Peugeot', 'Kisbee'),
    (2, 'O2Feel', 'iVog City'),
    (3, 'Renault', 'Twingo'),
    (4, 'Yamaha', 'NMAX');

INSERT INTO orders (id, id_user, id_pizza, id_format, timestamp_order, timestamp_deliver, final_price, id_deliver, id_vehicule) VALUES
    (1, 1, 1, 2, '2026-05-30 19:10:00', '2026-05-30 19:38:00', 900, 1, 1),
    (2, 1, 5, 3, '2026-06-01 20:05:00', NULL, 1608, NULL, NULL),
    (3, 2, 2, 1, '2026-06-01 12:30:00', '2026-06-01 12:55:00', 737, 2, 2),
    (4, 3, 7, 2, '2026-06-02 11:00:00', '2026-06-02 11:42:00', 0, 3, 3);

INSERT INTO receipts (id, id_order, id_user, final_price) VALUES
    (1, 1, 1, 900),
    (2, 2, 1, 1608),
    (3, 3, 2, 737),
    (4, 4, 3, 0);