CREATE TABLE IF NOT EXISTS orders
(
    id               UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    state            VARCHAR(250) NOT NULL,
    shopping_cart_id UUID NOT NULL,
    payment_id       UUID,
    delivery_id      UUID,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN DEFAULT false,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION
    );

CREATE TABLE IF NOT EXISTS order_structs
(
    id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id   UUID REFERENCES orders (id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity   INTEGER
    );