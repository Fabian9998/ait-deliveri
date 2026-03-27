package com.ait.deliveri.db.repository.imp;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ait.deliveri.db.entity.Order;
import com.ait.deliveri.db.repository.IOrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	private final IOrderRepository repository;
	private final PageRepository page;

	public Page<Order> findAll(Map<String, String> params, Pageable pageable) {
		return page.pagination(repository, Order.class, params, pageable);
	}

	public Optional<Order> findById(UUID id) {
		return repository.findById(id);
	}

	public Order save(Order entity) {
		return repository.save(entity);
	}
}
