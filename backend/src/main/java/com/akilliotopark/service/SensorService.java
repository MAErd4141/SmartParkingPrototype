package com.akilliotopark.service;

import com.akilliotopark.dto.SensorReadingRequest;
import com.akilliotopark.entity.ParkingSpot;
import com.akilliotopark.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SensorService {

    // ✅ LOT-001 sabit (şimdilik). Sonra deviceId -> lotId map yaparız.
    private static final UUID LOT_001_ID = UUID.fromString("8be945c1-be8c-4385-9380-951ddef3dbbf");

    private final AsyncLogService asyncLogService;
    private final ParkingSpotRepository parkingSpotRepository;

    private final Map<Integer, Boolean> slotStateCache = new ConcurrentHashMap<>();

    @Value("${sensor.threshold-cm:25}")
    private double thresholdCm;

    @Transactional
    public void handleReading(SensorReadingRequest req) {
        boolean occupied = req.distanceCm() < thresholdCm;

        // aynı state tekrar geldiyse DB’ye de log’a da girmeyelim
        Boolean previous = slotStateCache.get(req.slotId());
        if (previous != null && previous == occupied) {
            return;
        }
        slotStateCache.put(req.slotId(), occupied);

        String spotCode = toSpotCode(req.slotId()); // 1 -> A-1

        Optional<ParkingSpot> spotOpt =
                parkingSpotRepository.findByParkingLotIdAndSpotCode(LOT_001_ID, spotCode);

        if (spotOpt.isEmpty()) {
            asyncLogService.saveLog(
                    "Sensor-Service",
                    "SPOT_NOT_FOUND",
                    "Spot bulunamadı | slotId=" + req.slotId() + " | spotCode=" + spotCode,
                    Map.of("slotId", req.slotId(), "spotCode", spotCode, "parkingLotId", LOT_001_ID.toString())
            );
            return;
        }

        ParkingSpot spot = spotOpt.get();
        spot.setOccupied(occupied);
        parkingSpotRepository.save(spot);

        String type = occupied ? "SLOT_OCCUPIED" : "SLOT_EMPTY";
        String message = "Spot " + spotCode
                + (occupied ? " dolu" : " boş")
                + " | distance=" + req.distanceCm() + "cm"
                + " | device=" + req.deviceId();

        asyncLogService.saveLog(
                "Sensor-Service",
                type,
                message,
                Map.of(
                        "deviceId", req.deviceId(),
                        "slotId", req.slotId(),
                        "spotCode", spotCode,
                        "parkingLotId", LOT_001_ID.toString(),
                        "distanceCm", req.distanceCm(),
                        "thresholdCm", thresholdCm,
                        "occupied", occupied
                )
        );
    }

    private String toSpotCode(int slotId) {
        if (slotId < 1 || slotId > 10) {
            // şimdilik 10 spot test ediyoruz; istersen genişletiriz
            return "A-" + slotId;
        }
        return "A-" + slotId;
    }
}
