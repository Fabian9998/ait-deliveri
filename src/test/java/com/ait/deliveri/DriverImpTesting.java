package com.ait.deliveri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ait.deliveri.db.dto.DriverRequest;
import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.repository.imp.DriverRepository;
import com.ait.deliveri.exception.BadRequestException;
import com.ait.deliveri.exception.NotFoundException;
import com.ait.deliveri.service.implement.DriverImp;

@ExtendWith(MockitoExtension.class)
public class DriverImpTesting {

	private Driver driver;
	private DriverRequest driverRequest;
	private Pageable pageable;
	private Map<String, String> params;

	//Mock del repositorio
	@Mock
	private DriverRepository repository;

	//Instancia real
	@InjectMocks
	private DriverImp imp;
	
	//Datos para utilizar
	@BeforeEach
    void setUp() {
        driver = Driver.builder()
                .id(UUID.randomUUID())
                .name("Fabian Minor")
                .licenseNumber("ABC-123")
                .active(true)
                .build();
 
        driverRequest = DriverRequest.builder()
                .name("Fabian Minor")
                .licenseNumber("ABC-123")
                .build();
 
        pageable = PageRequest.of(0, 10);
        params = new HashMap<>();
    }

	@Test//Retorno exitoso cuando si existen conductores
	void get_retorno_conductores_existen() {
		Page<Driver> pageDriver = new PageImpl<>(List.of(driver), pageable, 1);

		when(repository.findAll(any(), any(Pageable.class))).thenReturn(pageDriver);

		ResponseEntity<?> response = imp.get(params, pageable);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test//No encontrado cuando no existen conductores
    void get_retorno_notfound() {
        Page<Driver> pageVacia = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(repository.findAll(any(), any(Pageable.class))).thenReturn(pageVacia);
 
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> imp.get(params, pageable) 
        );
 
        assertEquals("No encontrado", exception.getMessage());
    }
	
	@Test//Crear conductor cuando no existe la licencia
    void create_crear_conductor() {
        when(repository.existsByLicenseNumber("ABC-123")).thenReturn(false);
        when(repository.save(any(Driver.class))).thenReturn(driver);
 
        ResponseEntity<?> response = imp.create(driverRequest);
 
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
	
	@Test//No crea conductor porque existe la licencia
    void create_licencia_existente() {
        when(repository.existsByLicenseNumber("ABC-123")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> imp.create(driverRequest)
        );
 
        assertEquals("El numero de licencia ya se encuentra registrado", exception.getMessage());

        verify(repository, never()).save(any(Driver.class));
    }

}
