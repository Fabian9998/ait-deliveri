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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/driver")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Operaciones relacionadas con conductores")
public class DriverController {

	private final IDriverService service;
	
	@GetMapping
	@Operation(summary = "Obtener conductores", description = "Se pueden obtener conductores por parametros y paginado, el default del paginado son los primeros 10 y la pagina 1")
	public ResponseEntity<?> get(
			@Parameter(description = "Se añaden parametros para filtrar los conductores, este puede ir vacio.", example = "{\"name\":\"Juan\"}")
			@RequestParam Map<String, String> params,
			@Parameter(description = "Se añade la paginacion, si es vacio toma por defecto Tamaño 10 Pagina 0.", example = "{\"size\":2, \"page\":1}")
            @PageableDefault(size = 10, page = 0) Pageable pageable){
		return service.get(params, pageable);
	}
	
	@PostMapping
	@Operation(summary = "Crear conductor", description = "Se crea nuevo conductor")
	public ResponseEntity<?> create(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del conductor nombre y licencia", required = true)
			@Valid @RequestBody DriverRequest request){
		return service.create(request);
	}
	
	@PatchMapping("{id}/status")
	@Operation(summary = "Actualizar estatus", description = "Se cambia el estatus del conductor")
	public ResponseEntity<?> status(
			@Parameter(description = "UUID Del usuario al que se le cambiara el estatus", example = "3faef6cb-a6a1-408c-9a4e-9c2cca69d77e")
			@PathVariable UUID id){
		return service.status(id);
	}
}
