package com.akilliotopark.repository;

import com.akilliotopark.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TariffRepository extends JpaRepository<Tariff, UUID> {
}