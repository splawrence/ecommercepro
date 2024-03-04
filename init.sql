CREATE TABLE IF NOT EXISTS orders
(
    id bigint NOT NULL,
    created timestamp(6) without time zone NOT NULL,
    status character varying(255) COLLATE pg_catalog."default" NOT NULL,
    updated timestamp(6) without time zone NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products
(
    id bigint NOT NULL,
    created timestamp(6) without time zone NOT NULL,
    description character varying(255) COLLATE pg_catalog."default" NOT NULL,
    price numeric(38,2) NOT NULL,
    updated timestamp(6) without time zone NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    id bigint NOT NULL,
    created timestamp(6) without time zone NOT NULL,
    quantity integer NOT NULL,
    updated timestamp(6) without time zone NOT NULL,
    order_id bigint,
    product_id bigint,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id)
        REFERENCES orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_order_items_products FOREIGN KEY (product_id)
        REFERENCES products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

INSERT INTO products(
    id, created, description, price, updated)
    VALUES (2, NOW(), 'Off-brand Product', 50.00, NOW());
    
INSERT INTO products(
    id, created, description, price, updated)
    VALUES (3, NOW(), 'Designer Product', 75.00, NOW());
    
INSERT INTO orders(
    id, created, status, updated)
    VALUES (2, NOW(), 'Processing', NOW());
    
INSERT INTO orders(
    id, created, status, updated)
    VALUES (3, NOW(), 'Completed', NOW());
    
INSERT INTO order_items(
    id, created, quantity, updated, order_id, product_id)
    VALUES (2, NOW(), 2, NOW(), 2, 2);
    
INSERT INTO order_items(
    id, created, quantity, updated, order_id, product_id)
    VALUES (3, NOW(), 3, NOW(), 3, 3);
