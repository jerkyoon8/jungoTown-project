package com.juwon.springcommunity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class DbInitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createTables() {
        // 주문 테이블 생성
        String createOrders = "CREATE TABLE IF NOT EXISTS `orders` (" +
            "`id` BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "`user_id` BIGINT NOT NULL, " +
            "`product_id` BIGINT NOT NULL, " +
            "`order_uid` VARCHAR(255) NOT NULL UNIQUE, " +
            "`price` DECIMAL(10, 2) NOT NULL, " +
            "`status` VARCHAR(20) DEFAULT 'PENDING', " +
            "`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (`user_id`) REFERENCES `user`(`id`), " +
            "FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)" +
            ");";

        // 결제 테이블 생성
        String createPayments = "CREATE TABLE IF NOT EXISTS `payments` (" +
            "`id` BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "`order_id` BIGINT NOT NULL, " +
            "`imp_uid` VARCHAR(255) NOT NULL, " +
            "`pay_method` VARCHAR(50), " +
            "`amount` DECIMAL(10, 2) NOT NULL, " +
            "`status` VARCHAR(20) NOT NULL, " +
            "`paid_at` TIMESTAMP, " +
            "FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)" +
            ");";

        try {
            jdbcTemplate.execute(createOrders);
            System.out.println(">>> 'orders' table created successfully.");
            jdbcTemplate.execute(createPayments);
            System.out.println(">>> 'payments' table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw e; 
        }
    }
}
