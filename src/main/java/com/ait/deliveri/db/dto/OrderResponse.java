package com.ait.deliveri.db.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

	private UUID id;

	private OrderStatus status;

	private Driver driver;

	private String origin;

	private String destination;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
