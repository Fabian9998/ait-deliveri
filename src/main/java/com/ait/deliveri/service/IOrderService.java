package com.ait.deliveri.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.ait.deliveri.db.dto.OrderRequest;

public interface IOrderService {

	public ResponseEntity<?> get(Map<String, String> params,Pageable pageable);

	public ResponseEntity<?> getById(UUID id);

	public ResponseEntity<?> create(OrderRequest request);
	
	public ResponseEntity<?> asignDriver(UUID id, UUID driverId);
	
	public ResponseEntity<?> status(UUID id, String status);
	
}
