package com.ait.deliveri.db.dto;

import java.time.LocalDateTime;
import java.util.UUID;

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

	private String status;

	private DriverResponse driver;

	private String origin;

	private String destination;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
