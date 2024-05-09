INSERT INTO bank_accounts(currency, balance)
VALUES ('RUB', 5000),
       ('RUB', 10000);

INSERT INTO merchants(first_name, last_name, country, date_of_birth)
VALUES ('Alexandr', 'Shevtsov', 'Russia', '2000-02-16');

INSERT INTO merchants_bank_accounts(merchant_id, bank_account_id)
VALUES (1, 2);

INSERT INTO customers(first_name, last_name, country, date_of_birth)
VALUES ('Ivan', 'Ivanov', 'Russia', '1996-11-15');

INSERT INTO payment_cards(bank_account_id, card_number, expire_date, cvv, customer_id)
VALUES (1, '4102778822334893', '2026-01-08 04:05:06', '566', 1);