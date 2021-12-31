package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private String id ;
    private Integer moneyCharge ;
    private Integer totalMoneyProduct ;
    private Integer excessMoney ;
    private List<Product> lstProduct ;
}
