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

CREATE TABLE customers(
                          id INT NOT NULL AUTO_INCREMENT,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          balance INT NOT NULL,
                          PRIMARY KEY(id)
);

CREATE TABLE receipts(
                         id INT NOT NULL AUTO_INCREMENT,
                         id_order INT NOT NULL,
                         id_customer INT NOT NULL,
                         final_price INT NOT NULL,
                         PRIMARY KEY(id)
);

CREATE TABLE orders(
                       id INT NOT NULL AUTO_INCREMENT,
                       id_customer INT NOT NULL,
                       id_pizza INT NOT NULL,
                       id_size INT NOT NULL,
                       timestamp_order DATETIME NOT NULL,
                       timestamp_deliver DATETIME NOT NULL,
                       final_price INT NOT NULL,
                       id_deliver INT NOT NULL,
                       id_vehicule INT NOT NULL,
                       PRIMARY KEY(id)
);

CREATE TABLE delivers(
                         id INT NOT NULL AUTO_INCREMENT,
                         first_name VARCHAR(50) NOT NULL,
                         last_name VARCHAR(50) NOT NULL,
                         email VARCHAR(50) NOT NULL,
                         password VARCHAR(50) NOT NULL,
                         id_vehicule INT NOT NULL,
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
        FOREIGN KEY (id_customer)
            REFERENCES customers(id)
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
        FOREIGN KEY (id_size)
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
        FOREIGN KEY (id_customer)
            REFERENCES customers(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;

ALTER TABLE delivers
    ADD CONSTRAINT fk_vehicules_delivers
        FOREIGN KEY (id_vehicule)
            REFERENCES vehicules(id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;