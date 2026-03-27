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

import com.ait.deliveri.db.dto.OrderRequest;
import com.ait.deliveri.db.dto.OrderResponse;
import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.entity.Order;
import com.ait.deliveri.db.entity.OrderStatus;
import com.ait.deliveri.db.repository.imp.DriverRepository;
import com.ait.deliveri.db.repository.imp.OrderRepository;
import com.ait.deliveri.db.repository.imp.OrderStatusRepository;
import com.ait.deliveri.service.IOrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderImp implements IOrderService {
	
	private final OrderRepository repository;
	private final OrderStatusRepository statusRepository;
	private final DriverRepository driverRepository;
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> get(Map<String, String> params, Pageable pageable) {
		try {
            pageable = "*".equals(params.get("size")) ? PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort())
                    : pageable;
            Page<Order> page = repository.findAll(params, pageable);

            if (page.getContent().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No encontrado"));
            }

            List<OrderResponse> orders = page.getContent().stream().map(this::mapToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> data = Map.of(
                    "data", orders,
                    "paginas", page.getTotalPages(),
                    "total", page.getTotalElements());

            return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", data));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
        }
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> getById(UUID id) {
		try {
			Optional<Order> opt = repository.findById(id);
			if(opt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No encontrado"));
			}
			
			OrderResponse response = this.mapToResponse(opt.get());
			return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<?> create(OrderRequest request) {
		try {
			Optional<OrderStatus> statusOpt = statusRepository.findByCode("CREATED");
			if(statusOpt.isEmpty()) {
				return null;
			}
			
			Order entity = Order.builder()
					.status(statusOpt.get())
					.origin(request.getOrigin())
					.destination(request.getDestination())
					.build();
			entity = repository.save(entity);
			
			OrderResponse response = this.mapToResponse(entity);
			return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("codigo", 201, "mensaje", "Recurso creado", "data", response));
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
		}
	}
	
	@Override
	@Transactional
	public ResponseEntity<?> asignDriver(UUID id, UUID driverId) {
		try {
			Optional<Order> opt = repository.findById(id);
			if(opt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No encontrado"));
			}
			
			Order entity = opt.get();
			if(!entity.getStatus().getCode().equals("CREATED")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("codigo", 400, "mensaje", "La orden debe estar en estatus creada para asignar a un conductor"));
			}
			
			Optional<Driver> optDrivert = driverRepository.findById(driverId);
			if(optDrivert.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No se encontro el conductor"));
			}
			
			entity.setDriver(optDrivert.get());
			entity = repository.save(entity);
			
			OrderResponse response = this.mapToResponse(entity);
			return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<?> status(UUID id, String status) {
		try {
			Optional<Order> opt = repository.findById(id);
			if(opt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No encontrado"));
			}
			
			Order entity = opt.get();
			if(entity.getStatus().getCode().equals("DELIVERED") || entity.getStatus().getCode().equals("CANCELLED")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("codigo", 400, "mensaje", "No se puede cambiar el estatus a una orden entregada o cancelada"));
			}
			
			if(entity.getStatus().getCode().equals("IN_TRANSIT") && status.equals("CREATED")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("codigo", 400, "mensaje", "No se puede cambiar el estatus a creada a una orden en proceso de entrega"));
			}
			
			if(entity.getStatus().getCode().equals("CREATED") && status.equals("IN_TRANSIT") && entity.getDriver() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("codigo", 400, "mensaje", "Debe asignar un condutor para poner la orden en estatus de entrega"));
			}
			
			Optional<OrderStatus> optStatus = statusRepository.findByCode(status);
			if(optStatus.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("codigo", 404, "mensaje", "No se encontro el recurso"));
			}
			
			entity.setStatus(optStatus.get());
			entity = repository.save(entity);
			
			OrderResponse response = this.mapToResponse(entity);
			return ResponseEntity.ok(Map.of("codigo", 200, "mensaje", "Ok", "data", response));
		} catch (Exception e) {
			e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
		}
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
