CREATE TABLE IF NOT EXISTS shopping_cart (
         id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
         username VARCHAR NOT NULL,
         active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
          id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
          product_id UUID NOT NULL,
          quantity INTEGER,
          cart_id UUID REFERENCES shopping_cart (id) ON DELETE CASCADE
);