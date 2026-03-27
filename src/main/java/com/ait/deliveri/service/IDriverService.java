package com.ait.deliveri.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.ait.deliveri.db.dto.DriverRequest;

public interface IDriverService {

	public ResponseEntity<?> get(Map<String, String> params, Pageable pageable);
	
	public ResponseEntity<?> create(DriverRequest request);
	
	public ResponseEntity<?> status(UUID id);
}
