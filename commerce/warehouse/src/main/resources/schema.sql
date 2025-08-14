CREATE TABLE IF NOT EXISTS warehouse_products(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID,
    quantity INTEGER,
    fragile BOOLEAN,
    width DOUBLE PRECISION,
    height DOUBLE PRECISION,
    depth DOUBLE PRECISION,
    weight DOUBLE PRECISION
);