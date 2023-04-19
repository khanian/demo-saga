package com.khy.demosaga.repository;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "ORDER_SAGA_TABLE")
@Entity
public class OrderSagaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sagaId;

    @Column(name = "orderId")
    public Long orderId;

    @Column(name = "customerId")
    public Long customerId;

    @Column(name = "productId")
    public Long productId;

    @Column(name = "amount")
    public Long amount;

    @Column(name = "shippingAddress")
    public String shippingAddress;

    @Column(name = "currentState")
    public String currentState;

    @Column(name = "eventAt")
    private LocalDateTime eventAt;
}
