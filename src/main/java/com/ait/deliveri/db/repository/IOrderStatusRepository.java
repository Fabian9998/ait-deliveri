package com.ait.deliveri.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ait.deliveri.db.entity.OrderStatus;
import java.util.Optional;


public interface IOrderStatusRepository extends JpaRepository<OrderStatus, Integer>, JpaSpecificationExecutor<OrderStatus> {

	Optional<OrderStatus> findByCode(String code);
}
