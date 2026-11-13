CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL
    );
INSERT INTO products (name, price)
SELECT
    (ARRAY['Smart', 'Wireless', 'Ergonomic', 'Portable', 'Ultra', 'Gaming', 'Professional', 'Compact', 'Heavy Duty', 'Digital'])[floor(random() * 10 + 1)] || ' ' ||
    (ARRAY['Laptop', 'Mouse', 'Keyboard', 'Monitor', 'Headphones', 'Speaker', 'Microphone', 'Webcam', 'Charger', 'USB Hub', 'Tablet', 'Smartphone', 'Router', 'External SSD', 'HDMI Cable'])[floor(random() * 15 + 1)] || ' #' || i,

    ROUND((RANDOM() * 1000 + 9.99)::numeric, 2)
FROM generate_series(1, 5000) AS i;