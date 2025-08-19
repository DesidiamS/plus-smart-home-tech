CREATE TABLE IF NOT EXISTS payments
(
    id               UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id         UUID NOT NULL,
    payment_total    DOUBLE PRECISION,
    products_total   DOUBLE PRECISION,
    delivery_total   DOUBLE PRECISION,
    fee_total        DOUBLE PRECISION,
    status           VARCHAR(250)
);