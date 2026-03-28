package com.ait.deliveri.service.implement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ait.deliveri.db.dto.DriverRequest;
import com.ait.deliveri.db.dto.DriverResponse;
import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.repository.imp.DriverRepository;
import com.ait.deliveri.exception.BadRequestException;
import com.ait.deliveri.exception.NotFoundException;
import com.ait.deliveri.service.IDriverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverImp implements IDriverService {
	
	private final DriverRepository repository;
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> get(Map<String, String> params, Pageable pageable) {
		pageable = "*".equals(params.get("size")) ? PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort()) : pageable;
		Page<Driver> page = repository.findAll(params, pageable);

		if (page.getContent().isEmpty()) {
			throw new NotFoundException("No encontrado");
		}

		List<DriverResponse> drivers = page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

		Map<String, Object> data = Map.of(
				"data", drivers, 
				"paginas", page.getTotalPages(), 
				"total", page.getTotalElements());

		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", data));
	}

	@Override
	@Transactional
	public ResponseEntity<?> create(DriverRequest request) {
		Boolean license = repository.existsByLicenseNumber(request.getLicenseNumber());
		if (license) {
			throw new BadRequestException("El numero de licencia ya se encuentra registrado");
		}

		Driver entity = new Driver();
		entity.setName(request.getName());
		entity.setLicenseNumber(request.getLicenseNumber());
		entity.setActive(true);
		entity = repository.save(entity);

		DriverResponse reponse = this.mapToResponse(entity);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("codigo", 201, "mensaje", "Recurso creado", "data", reponse));
	}

	@Override
	@Transactional
	public ResponseEntity<?> status(UUID id) {
		Optional<Driver> opt = repository.findById(id);
		if (opt.isEmpty()) {
			throw new NotFoundException("No encontrado");
		}

		Driver entity = opt.get();
		entity.setActive(!entity.getActive());
		entity = repository.save(entity);

		DriverResponse reponse = this.mapToResponse(entity);
		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", reponse));
	}
	
	private DriverResponse mapToResponse(Driver entity) {
		return DriverResponse.builder()
				.id(entity.getId())
				.name(entity.getName())
				.licenseNumber(entity.getLicenseNumber())
				.active(entity.getActive())
				.build();
	}

}
