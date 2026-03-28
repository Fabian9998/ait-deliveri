package com.ait.deliveri.web.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ait.deliveri.db.dto.AssignDriverRequest;
import com.ait.deliveri.db.dto.OrderRequest;
import com.ait.deliveri.service.IOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {
	
	private final IOrderService service;

	@GetMapping
	public ResponseEntity<?> get(@RequestParam Map<String, String> params,
            @PageableDefault(size = 10, page = 0) Pageable pageable){
		return service.get(params, pageable);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getById(@PathVariable UUID id){
		return service.getById(id);
	}
	
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody OrderRequest request){
		return service.create(request);
	}
	
	@PatchMapping("{id}/driver/{id}")
	public ResponseEntity<?> assignDriver(@PathVariable UUID id, @PathVariable UUID driverId, @Valid AssignDriverRequest request){
		return service.assignDriver(id, driverId, request);
	}
	
	@PatchMapping("{id}/status/{status}")
	public ResponseEntity<?> status(@PathVariable UUID id, @PathVariable String status){
		return service.status(id, status);
	}
}
