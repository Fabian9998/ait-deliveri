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

import com.ait.deliveri.db.dto.DriverRequest;
import com.ait.deliveri.service.IDriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/driver")
@RequiredArgsConstructor
public class DriverController {

	private final IDriverService service;
	
	@GetMapping
	public ResponseEntity<?> get(@RequestParam Map<String, String> params,
            @PageableDefault(size = 10, page = 0) Pageable pageable){
		return service.get(params, pageable);
	}
	
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody DriverRequest request){
		return service.create(request);
	}
	
	@PatchMapping("{id}/status")
	public ResponseEntity<?> status(@PathVariable UUID id){
		return service.status(id);
	}
}
