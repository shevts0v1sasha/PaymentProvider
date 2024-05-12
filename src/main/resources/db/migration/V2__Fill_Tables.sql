INSERT INTO bank_accounts(currency, balance, owner_uid)
VALUES ('RUB', 5000, '7ddd5096-bc8b-4ec9-a23c-5bd239dd893e'),
       ('RUB', 10000, 'e15d2c9c-b9b7-44ce-9964-b28208edc8fa');

INSERT INTO merchants(id, first_name, last_name, country, date_of_birth)
VALUES ('e15d2c9c-b9b7-44ce-9964-b28208edc8fa', 'Alexandr', 'Shevtsov', 'Russia', '2000-02-16');

INSERT INTO customers(first_name, last_name, country, date_of_birth)
VALUES ('Ivan', 'Ivanov', 'Russia', '1996-11-15');

INSERT INTO payment_cards(id, card_number, expire_date, cvv, customer_id)
VALUES ('7ddd5096-bc8b-4ec9-a23c-5bd239dd893e', '4102778822334893', '2026-01-08 04:05:06', '566', 1);