package com.akilliotopark.mapper;

import com.akilliotopark.dto.ParkingLotResponse;
import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.entity.TariffRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingLotMapper {

    @Mapping(target = "hourlyRate", source = "entity", qualifiedByName = "extractBasePrice")
    ParkingLotResponse toResponseDto(ParkingLot entity);

    List<ParkingLotResponse> toResponseDtoList(List<ParkingLot> entities);

    @Named("extractBasePrice")
    default BigDecimal extractBasePrice(ParkingLot lot) {
        if (lot.getTariff() != null && lot.getTariff().getRules() != null && !lot.getTariff().getRules().isEmpty()) {
            return lot.getTariff().getRules().stream()
                    .filter(r -> r.getMaxMinutes() >= 60)
                    .findFirst()
                    .map(TariffRule::getPrice)
                    .orElse(lot.getTariff().getRules().get(0).getPrice());
        }
        return BigDecimal.ZERO;
    }
}