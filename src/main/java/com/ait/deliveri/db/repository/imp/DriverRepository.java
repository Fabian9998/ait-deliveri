package com.ait.deliveri.db.repository.imp;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.ait.deliveri.db.entity.Driver;
import com.ait.deliveri.db.repository.IDriverRepository;

@Repository
@RequiredArgsConstructor
public class DriverRepository {

	private final IDriverRepository repository;
	private final PageRepository page;

	public Page<Driver> findAll(Map<String, String> params, Pageable pageable) {
		return page.pagination(repository, Driver.class, params, pageable);
	}

	public Optional<Driver> findById(UUID id) {
		return repository.findById(id);
	}

	public Boolean existsByLicenseNumber(String licenseNumber) {
		return repository.existsByLicenseNumber(licenseNumber);
	}

	public Driver save(Driver entity) {
		return repository.save(entity);
	}
}
