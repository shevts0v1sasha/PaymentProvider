CREATE TABLE bank_accounts
(
    id       SERIAL PRIMARY KEY,
    currency VARCHAR(16) NOT NULL,
    balance  NUMERIC     NOT NULL DEFAULT 0 CHECK ( balance >= 0 )
);

CREATE TABLE merchants
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(64) NOT NULL,
    last_name     VARCHAR(64) NOT NULL,
    country       VARCHAR(32) NOT NULL,
    date_of_birth DATE
);

CREATE TABLE merchants_bank_accounts
(
    id              SERIAL PRIMARY KEY,
    merchant_id     INTEGER NOT NULL REFERENCES merchants (id),
    bank_account_id INTEGER NOT NULL REFERENCES bank_accounts (id),
    UNIQUE (merchant_id, bank_account_id)
);

CREATE TABLE customers
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(64) NOT NULL,
    last_name     VARCHAR(64) NOT NULL,
    country       VARCHAR(32) NOT NULL,
    date_of_birth DATE
);

CREATE TABLE payment_cards
(
    id              SERIAL PRIMARY KEY,
    bank_account_id INTEGER NOT NULL REFERENCES bank_accounts (id),
    card_number     VARCHAR(64),
    expire_date     TIMESTAMP,
    cvv             VARCHAR(3),
    customer_id     INTEGER NOT NULL REFERENCES customers (id)
);

CREATE TABLE transactions
(
    id                   VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_status   VARCHAR(16) NOT NULL,
    transaction_type     VARCHAR(16) NOT NULL,
    from_bank_account_id INTEGER     NOT NULL REFERENCES bank_accounts (id),
    to_bank_account_id   INTEGER     NOT NULL REFERENCES bank_accounts (id),
    amount               NUMERIC     NOT NULL CHECK ( amount > 0 ),
    created_at           TIMESTAMP   NOT NULL,
    updated_at           TIMESTAMP   NOT NULL,
    language             VARCHAR(3)  NOT NULL
);

CREATE TABLE webhooks
(
    id                 SERIAL PRIMARY KEY,
    transaction_id     VARCHAR(36) REFERENCES transactions (id),
    invocation_date    TIMESTAMP    NOT NULL,
    message            TEXT         NOT NULL,
    notification_url   VARCHAR(256) NOT NULL,
    transaction_status VARCHAR(32)  NOT NULL
);
