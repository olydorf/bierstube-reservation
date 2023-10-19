-- Drop tables
DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS restaurant_table CASCADE;

-- Recreate restaurant_table
CREATE TABLE restaurant_table (
                                  table_number INTEGER PRIMARY KEY,
                                  capacity INTEGER
);

-- Recreate reservation
CREATE TABLE reservation (
                             id SERIAL PRIMARY KEY,
                             table_id INTEGER REFERENCES restaurant_table(table_number),
                             start_time TIMESTAMP,
                             end_time TIMESTAMP,
                             name VARCHAR(255),
                             message VARCHAR(255),
                             email VARCHAR(255),
                             confirmed BOOLEAN,
                             amount_guests INTEGER
);