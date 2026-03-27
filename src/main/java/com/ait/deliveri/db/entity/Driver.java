package com.ait.deliveri.db.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
	
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(name = "license_number", nullable = false, unique = true)
	private String licenseNumber;

	@Column(nullable = false)
	private Boolean active;
}
