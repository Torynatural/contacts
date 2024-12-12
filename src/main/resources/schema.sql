CREATE TABLE `contacts` (
                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                            `name` VARCHAR(50) NOT NULL,
                            `phone` VARCHAR(20) NOT NULL,
                            `email` VARCHAR(100) DEFAULT NULL,
                            `avatar` VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;