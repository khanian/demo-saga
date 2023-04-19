package com.khy.demosaga.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSagaRepository extends JpaRepository<OrderSagaEntity, Long> {
}
