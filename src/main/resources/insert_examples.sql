USE rapizz;

INSERT INTO ingredients (name) VALUES ('Mozzarella');

INSERT INTO vehicules (brand, model) VALUES ('Yamaha', 'NMAX 125');

INSERT INTO delivers (first_name, last_name, email, password)
VALUES ('Jean', 'Dupont', 'jean.dupont@rapizz.fr', 'motdepasse123');


INSERT INTO pizzas (name, price) VALUES ('Margherita', 500);

INSERT INTO pizza_ingredients (id_pizza, id_ingredient) VALUES (1, 1);
