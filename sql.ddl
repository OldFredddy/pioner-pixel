
CREATE TABLE users_p (
                         id            BIGSERIAL PRIMARY KEY,
                         name          VARCHAR(500) NOT NULL,
                         date_of_birth DATE NOT NULL,
                         password      VARCHAR(500) NOT NULL
);

CREATE TABLE accounts (
                          id              BIGSERIAL PRIMARY KEY,
                          user_id         BIGINT UNIQUE NOT NULL REFERENCES users_p(id),
                          balance         NUMERIC(14,2) NOT NULL,
                          initial_balance NUMERIC(14,2) NOT NULL
);

CREATE TABLE email_data (
                            id      BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL REFERENCES users_p(id),
                            email   VARCHAR(200) UNIQUE NOT NULL
);

CREATE TABLE phone_data (
                            id      BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL REFERENCES users_p(id),
                            phone   VARCHAR(13) UNIQUE NOT NULL
);

-- индексы под поиск
CREATE INDEX ix_users_p_name  ON users_p  (lower(name));
CREATE INDEX ix_email_email   ON email_data (email);
CREATE INDEX ix_phone_phone   ON phone_data (phone);