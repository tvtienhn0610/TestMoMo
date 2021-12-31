package com.example.demo.repository;

import com.example.demo.entity.Money;
import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository extends JpaRepository<Money, Long> {
    Money findByPriceAndActive(Integer price , Integer active);
}
