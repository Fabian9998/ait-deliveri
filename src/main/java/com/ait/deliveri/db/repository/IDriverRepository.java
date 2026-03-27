package com.ait.deliveri.db.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ait.deliveri.db.entity.Driver;

public interface IDriverRepository extends JpaRepository<Driver, UUID>, JpaSpecificationExecutor<Driver> {

	Boolean existsByLicenseNumber(String licenseNumber);
}
