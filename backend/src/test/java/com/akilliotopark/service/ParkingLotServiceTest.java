package com.akilliotopark.service;

import com.akilliotopark.dto.ParkingLotRequest;
import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.entity.Tariff;
import com.akilliotopark.mapper.ParkingLotMapper;
import com.akilliotopark.mapper.ParkingSpotMapper;
import com.akilliotopark.repository.ParkingLotRepository;
import com.akilliotopark.repository.ParkingSpotRepository;
import com.akilliotopark.repository.TariffRepository;
import com.akilliotopark.repository.TariffRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock private ParkingLotRepository parkingLotRepository;
    @Mock private ParkingSpotRepository parkingSpotRepository;
    @Mock private TariffRepository tariffRepository;
    @Mock private TariffRuleRepository tariffRuleRepository;
    @Mock private ParkingLotMapper parkingLotMapper;
    @Mock private ParkingSpotMapper parkingSpotMapper;

    @InjectMocks
    private ParkingLotService parkingLotService;

    @Test
    void createParkingLot_ShouldSave_WhenValidRequest() {
        // 1. HAZIRLIK
        ParkingLotRequest request = new ParkingLotRequest();
        request.setName("Test AVM");
        request.setCode("TR-TEST");
        request.setCapacity(10);
        request.setHourlyRate(BigDecimal.valueOf(50.0));

        ParkingLot savedLot = ParkingLot.builder()
                .id(UUID.randomUUID())
                .name("Test AVM")
                .build();

        when(tariffRepository.save(any(Tariff.class))).thenReturn(new Tariff());
        when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(savedLot);

        ParkingLot result = parkingLotService.createParkingLot(request);

        assertNotNull(result);
        assertEquals("Test AVM", result.getName());

        verify(parkingLotRepository).save(any(ParkingLot.class));
        verify(parkingSpotRepository, times(10)).save(any());
    }

    @Test
    void getAll_ShouldReturnList() {
        when(parkingLotRepository.findAll()).thenReturn(Collections.emptyList());

        parkingLotService.getAll();

        verify(parkingLotRepository).findAll();
        verify(parkingLotMapper).toResponseDtoList(any());
    }
}