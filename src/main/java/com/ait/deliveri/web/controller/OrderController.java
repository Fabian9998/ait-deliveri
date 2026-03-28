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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operaciones relacionadas con ordenes")
public class OrderController {
	
	private final IOrderService service;

	@GetMapping
	@Operation(summary = "Obtener ordenes", description = "Se pueden obtener conductores por parametros y paginado, el default del paginado son los primeros 10 y la pagina 1")
	public ResponseEntity<?> get(
			@Parameter(description = "Se añaden parametros para filtrar las ordenes, este puede ir vacio.", example = "{\"origin\":\"Av Palafox num24\"}")
			@RequestParam Map<String, String> params,
			@Parameter(description = "Se añade la paginacion, si es vacio toma por defecto Tamaño 10 Pagina 0.", example = "{\"size\":2, \"page\":1}")
            @PageableDefault(size = 10, page = 0) Pageable pageable){
		return service.get(params, pageable);
	}
	
	@GetMapping("{id}")
	@Operation(summary = "Obtener orden", description = "Buscar orden por id")
	public ResponseEntity<?> getById(
			@Parameter(description = "UUID de la orden que se solicita buscar", example = "3faef6cb-a6a1-408c-9a4e-9c2cca69d77e")
			@PathVariable UUID id){
		return service.getById(id);
	}
	
	@PostMapping
	@Operation(summary = "Crear orden", description = "Crear nueva orden")
	public ResponseEntity<?> create(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la orden origen y destino", required = true)
			@Valid @RequestBody OrderRequest request){
		return service.create(request);
	}
	
	@PatchMapping("{id}/driver/{driverId}")
	@Operation(summary = "Asignar conductor a orden", description = "Se asigna conductor a una orden")
	public ResponseEntity<?> assignDriver(
			@Parameter(description = "UUID de la orden a la que se le asginara conductor", example = "3faef6cb-a6a1-408c-9a4e-9c2cca69d77e")
			@PathVariable UUID id,
			@Parameter(description = "UUID del conductor a asignar", example = "3faef6cb-a6a1-408c-9a4e-9c2cca69d77e")
			@PathVariable UUID driverId, 
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PDF e IMAGEN en base64", required = true)
			@Valid AssignDriverRequest request){
		return service.assignDriver(id, driverId, request);
	}
	
	@PatchMapping("{id}/status/{status}")
	@Operation(summary = "Actualizar estatus", description = "Actualizar estatus de la orden")
	public ResponseEntity<?> status(
			@Parameter(description = "UUID de la orden a la que se le actualizara el estatusr", example = "3faef6cb-a6a1-408c-9a4e-9c2cca69d77e")
			@PathVariable UUID id, 
			@Parameter(description = "Clave del estatus", example = "IN_TRANSIT o DELIVERED o CANCELLED")
			@PathVariable String status){
		return service.status(id, status);
	}
}
