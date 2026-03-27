package com.ait.deliveri.db.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ait.deliveri.db.entity.Order;

public interface IOrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

}
