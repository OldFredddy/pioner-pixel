

----------------------------------------------------------------------
-- user #1: Иван
----------------------------------------------------------------------
INSERT INTO users_p (id, name, date_of_birth, password)
VALUES (1, 'Ivan Petrov', '1990-05-01',
        '$2b$10$SOQIKq7bem.CApki3LZ5GuM5PYHhpC75bpnV98eO6Dk3WGxWvWcBy');  -- pwd: pionerpixel

INSERT INTO accounts   VALUES (1, 1, 100.00, 100.00);
INSERT INTO email_data VALUES (1, 1, 'ivan-megaman@gmail.com');
INSERT INTO phone_data VALUES (1, 1, '78005553535');

----------------------------------------------------------------------
-- user #2: Ольга
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (2, 'Olga Ivanova', '1998-11-20',
     '$2a$10$Q.1V9SYc0Pz0jzHCVaQS7.qhie1esZ1WlLTHt6OCB5oUQSfN7G7tq');        -- pwd: 87654321
INSERT INTO accounts   VALUES (2, 2, 250.00, 250.00);
INSERT INTO email_data VALUES (2, 2, 'olga_12341234@protonmail.com');
INSERT INTO phone_data VALUES (2, 2, '711111111111');

----------------------------------------------------------------------
-- user #3: Сергей
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (3, 'Sergey Smirnov', '1995-02-14',
     '$2a$10$fVH8e28OQRj9tqiDXs1e1uEvN8WcN6iYG3PaY5/J9OQj40YIfRnbK');        -- pwd: 12341234
INSERT INTO accounts   VALUES (3, 3, 150.00, 150.00);
INSERT INTO email_data VALUES (3, 3, 'sergey@example.com');
INSERT INTO phone_data VALUES (3, 3, '79001234567');

----------------------------------------------------------------------
-- user #4: Анна
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (4, 'Anna Petrova', '1992-07-30',
     '$2a$10$7EqJtq98hPqEX7fNZaFWoOaZ6u1O0wzyGuDIG5mDw4.58/LkN1U02');        -- pwd: 43214321
INSERT INTO accounts   VALUES (4, 4, 320.00, 320.00);
INSERT INTO email_data VALUES (4, 4, 'anna@example.com');
INSERT INTO phone_data VALUES (4, 4, '79201234567');

----------------------------------------------------------------------
-- user #5: Дмитрий
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (5, 'Dmitry Kuznetsov', '1985-12-05',
     '$2a$10$1b1e3Pc9W/b.HjgIueJ6auP6R.WvQhYB8O1JcQsL7Xfn3XSFqK5lS');        -- pwd: qwerty123
INSERT INTO accounts   VALUES (5, 5, 500.00, 500.00);
INSERT INTO email_data VALUES (5, 5, 'dmitry@example.com');
INSERT INTO phone_data VALUES (5, 5, '79301234567');

----------------------------------------------------------------------
-- user #6: Мария
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (6, 'Maria Sokolova', '1999-03-22',
     '$2a$10$E6i805w.MzE0iSWm4zdiROujw1JeYfW8dw19D86xtCWeREpO8Y9my');        -- pwd: password1
INSERT INTO accounts   VALUES (6, 6,  80.00,  80.00);
INSERT INTO email_data VALUES (6, 6, 'maria@example.com');
INSERT INTO phone_data VALUES (6, 6, '79601234567');

----------------------------------------------------------------------
-- user #7: Алексей
----------------------------------------------------------------------
INSERT INTO users_p VALUES
    (7, 'Alexey Popov', '1980-09-11',
     '$2a$10$X305qD3F1NAGTKoayh7eA.z3BpfT3.hB35GKB.nPzN.PVJ8yItG6e');        -- pwd: welcome
INSERT INTO accounts   VALUES (7, 7, 1000.00, 1000.00);
INSERT INTO email_data VALUES (7, 7, 'alexey@example.com');
INSERT INTO phone_data VALUES (7, 7, '79771234567');
