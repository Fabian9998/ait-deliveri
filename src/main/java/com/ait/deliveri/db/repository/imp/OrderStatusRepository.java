package com.ait.deliveri.db.repository.imp;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ait.deliveri.db.entity.OrderStatus;
import com.ait.deliveri.db.repository.IOrderStatusRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderStatusRepository {

	private final IOrderStatusRepository repository;
	
	public Optional<OrderStatus> findByCode(String code){
		return repository.findByCode(code);
	}
}
