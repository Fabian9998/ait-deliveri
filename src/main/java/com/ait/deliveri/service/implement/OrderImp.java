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

import com.ait.deliveri.db.dto.AssignDriverRequest;
import com.ait.deliveri.db.dto.OrderRequest;
import com.ait.deliveri.db.dto.OrderResponse;
import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.entity.Order;
import com.ait.deliveri.db.entity.OrderStatus;
import com.ait.deliveri.db.repository.imp.DriverRepository;
import com.ait.deliveri.db.repository.imp.OrderRepository;
import com.ait.deliveri.db.repository.imp.OrderStatusRepository;
import com.ait.deliveri.exception.BadRequestException;
import com.ait.deliveri.exception.NotFoundException;
import com.ait.deliveri.service.IOrderService;
import com.ait.deliveri.utils.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderImp implements IOrderService {
	
	private final OrderRepository repository;
	private final OrderStatusRepository statusRepository;
	private final DriverRepository driverRepository;
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> get(Map<String, String> params, Pageable pageable) {
		log.info("Buscar ordenes con parametros: {}", params);
		pageable = "*".equals(params.get("size")) ? PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort()) : pageable;
		Page<Order> page = repository.findAll(params, pageable);

		if (page.getContent().isEmpty()) {
			log.warn("No se encontraron ordenes");
			throw new NotFoundException("No encontrado");
		}

		List<OrderResponse> orders = page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

		log.info("Se encontraron ordenes");
		Map<String, Object> data = Map.of(
				"data", orders, 
				"paginas", page.getTotalPages(), 
				"total", page.getTotalElements());

		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", data));
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> getById(UUID id) {
		log.info("Buscar orden por id: {}", id);
		Optional<Order> opt = repository.findById(id);
		if (opt.isEmpty()) {
			log.warn("No se encontro la orden");
			throw new NotFoundException("No encontrado");
		}

		log.info("Orden encontrada");
		OrderResponse response = this.mapToResponse(opt.get());
		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));

	}

	@Override
	@Transactional
	public ResponseEntity<?> create(OrderRequest request) {
		log.info("Crear nueva orden con origen {} y destino {}", request.getOrigin(), request.getDestination());
		Optional<OrderStatus> statusOpt = statusRepository.findByCode("CREATED");
		if (statusOpt.isEmpty()) {
			log.warn("No se puede crear la orden por falta del recurso status CREATED");
			throw new NotFoundException("No se encontro recurso");
		}

		Order entity = Order.builder()
				.status(statusOpt.get())
				.origin(request.getOrigin())
				.destination(request.getDestination())
				.build();
		entity = repository.save(entity);

		log.info("Orden creada correctamente");
		OrderResponse response = this.mapToResponse(entity);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(Map.of("codigo", 201, "mensaje", "Recurso creado", "data", response));
	}
	
	@Override
	@Transactional
	public ResponseEntity<?> assignDriver(UUID id, UUID driverId, AssignDriverRequest request) {
		log.info("Asignar a la orden {} el coductor {}", id, driverId);
		Optional<Order> opt = repository.findById(id);
		if (opt.isEmpty()) {
			log.warn("No se encontro la orden");
			throw new NotFoundException("No encontrado");
		}

		Order entity = opt.get();
		if (!entity.getStatus().getCode().equals("CREATED")) {
			log.warn("Orden con el estatus CREATED");
			throw new BadRequestException("La orden debe estar en estatus creada para asignar a un conductor");
		}

		Optional<Driver> optDrivert = driverRepository.findById(driverId);
		if (optDrivert.isEmpty()) {
			log.warn("No se encontro el conductor");
			throw new NotFoundException("No se encontro el conductor");
		}

		if (!optDrivert.get().getActive()) {
			log.warn("Conductor inhabilitado");
			throw new BadRequestException("No se puede asignar un conductor inhabilitado");
		}

		String pdfPath = FileUtils.saveBase64File(entity.getId().toString(), "file" + entity.getId(), request.getPdf());
		String imagePath = FileUtils.saveBase64File(entity.getId().toString(), "img" + entity.getId(),
				request.getImage());

		log.info("Se guardo con exito los archivos");
		entity.setDriver(optDrivert.get());
		entity.setPdf(pdfPath);
		entity.setImage(imagePath);
		entity = repository.save(entity);

		log.info("Asignacion correcta del coductor a la orden");
		OrderResponse response = this.mapToResponse(entity);
		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));
	}

	@Override
	@Transactional
	public ResponseEntity<?> status(UUID id, String status) {
		log.info("Cambio de status de la orden {} al status {}", id, status);
		Optional<Order> opt = repository.findById(id);
		if (opt.isEmpty()) {
			log.warn("No se encontro la orden");
			throw new NotFoundException("No encontrado");
		}

		Order entity = opt.get();
		if (entity.getStatus().getCode().equals("DELIVERED") || entity.getStatus().getCode().equals("CANCELLED")) {
			log.warn("No se puede cambiar el estatus de la Orden {}", entity.getStatus().getCode());
			throw new BadRequestException("No se puede cambiar el estatus a una orden entregada o cancelada");
		}

		if (entity.getStatus().getCode().equals("IN_TRANSIT") && status.equals("CREATED")) {
			log.warn("No se puede cambiar una orden en proceso de entrega a creada");
			throw new BadRequestException("No se puede cambiar el estatus a creada a una orden en proceso de entrega");
		}

		if (entity.getStatus().getCode().equals("CREATED") && status.equals("IN_TRANSIT")
				&& entity.getDriver() == null) {
			log.warn("Debe asignar un conductor a la orden para poner la orden en proceso de entrega");
			throw new BadRequestException("Debe asignar un condutor para poner la orden en proceso de entrega");
		}

		Optional<OrderStatus> optStatus = statusRepository.findByCode(status);
		if (optStatus.isEmpty()) {
			log.warn("No se encontro el recurso del estatus");
			throw new NotFoundException("No se encontro recurso");
		}

		entity.setStatus(optStatus.get());
		entity = repository.save(entity);

		OrderResponse response = this.mapToResponse(entity);
		log.info("Estatus de la orden actualizada correctamente");
		return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));
	}
	
	private OrderResponse mapToResponse(Order entity) {
		return OrderResponse.builder()
				.id(entity.getId())
				.status(entity.getStatus())
				.driver(entity.getDriver())
				.origin(entity.getOrigin())
				.destination(entity.getDestination())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
	}

}
