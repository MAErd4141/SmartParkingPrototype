package com.akilliotopark.repository;

import com.akilliotopark.entity.TariffRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TariffRuleRepository extends JpaRepository<TariffRule, UUID> {
}