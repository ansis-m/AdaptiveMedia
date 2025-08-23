CREATE TABLE fetch_log (
                           id BIGSERIAL PRIMARY KEY,
                           fetch_date DATE NOT NULL
);

INSERT INTO fetch_log (fetch_date) VALUES ('2025-08-01');