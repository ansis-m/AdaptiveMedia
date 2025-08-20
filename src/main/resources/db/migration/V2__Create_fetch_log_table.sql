CREATE TABLE fetch_log (
                           id BIGSERIAL PRIMARY KEY,
                           fetch_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO fetch_log (fetch_timestamp) VALUES ('2000-01-01 00:00:00');