package com.ait.deliveri.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ait.deliveri.db.entity.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer>, JpaSpecificationExecutor<OrderStatus> {

}
