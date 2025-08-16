CREATE TABLE IF NOT EXISTS addresses
(
    id          UUID DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    country     VARCHAR,
    city        VARCHAR,
    street      VARCHAR,
    house       VARCHAR,
    flat        VARCHAR
);

CREATE TABLE IF NOT EXISTS deliveries
(
    id               UUID DEFAULT GEN_RANDOM_UUID() PRIMARY KEY,
    from_address     UUID REFERENCES addresses (id),
    to_address       UUID REFERENCES addresses (id),
    order_id         UUID,
    delivery_state   VARCHAR(50)
);