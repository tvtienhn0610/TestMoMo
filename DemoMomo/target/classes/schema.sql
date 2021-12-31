DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS money;

CREATE TABLE product
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(250) NOT NULL,
    price  INT          NOT NULL,
    number INT          NOT NULL,
    active INT          NOT NULL,
    quantily INT
);

CREATE TABLE money
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(250) NOT NULL,
    price  INT          NOT NULL,
    number INT          NOT NULL,
    active INT          NOT NULL
);

