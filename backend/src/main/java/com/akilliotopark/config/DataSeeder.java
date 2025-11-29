package com.akilliotopark.config;

import com.akilliotopark.entity.*;
import com.akilliotopark.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final TariffRepository tariffRepository;
    private final TariffRuleRepository tariffRuleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.findByEmail("admin@akilliotopark.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@akilliotopark.com")
                    .password(passwordEncoder.encode("123"))
                    .fullName("Süper Yönetici")
                    .role(UserRole.SUPERVISOR)
                    .district("Merkez")
                    .province("Istanbul")
                    .build();
            userRepository.save(admin);
            System.out.println("✅ SUPERVISOR OLUŞTURULDU: admin@akilliotopark.com");
        }

        if (tariffRepository.count() == 0) {
            Tariff tariff = Tariff.builder().name("AVM Standart Tarife").build();
            tariffRepository.save(tariff);

            tariffRuleRepository.save(TariffRule.builder()
                    .tariff(tariff)
                    .minMinutes(0)
                    .maxMinutes(60)
                    .price(BigDecimal.valueOf(40.0))
                    .build());

            tariffRuleRepository.save(TariffRule.builder()
                    .tariff(tariff)
                    .minMinutes(60)
                    .maxMinutes(120)
                    .price(BigDecimal.valueOf(70.0))
                    .build());

            tariffRuleRepository.save(TariffRule.builder()
                    .tariff(tariff)
                    .minMinutes(120)
                    .maxMinutes(1440)
                    .price(BigDecimal.valueOf(150.0))
                    .build());

            System.out.println("✅ TARİFE VE KURALLAR OLUŞTURULDU");

            if (parkingLotRepository.count() == 0) {
                ParkingLot lot = ParkingLot.builder()
                        .code("TR-34-01")
                        .name("Merkez AVM Otopark")
                        .address("Bağdat Cad. No:5")
                        .district("Kadikoy")
                        .province("Istanbul")
                        .latitude(40.9801).longitude(29.0302)
                        .tariff(tariff)
                        .build();

                ParkingLot savedLot = parkingLotRepository.save(lot);

                for (int i = 1; i <= 5; i++) {
                    createSpot(savedLot, "A-" + i, SpotType.STANDARD, false);
                }

                createSpot(savedLot, "E-1", SpotType.EV_CHARGING, true);
                createSpot(savedLot, "E-2", SpotType.EV_CHARGING, true);
                createSpot(savedLot, "D-1", SpotType.HANDICAPPED, false);

                System.out.println("✅ OTOPARK VE PARK YERLERİ (EV/ENGELİ) OLUŞTURULDU");
            }
        }
    }

    private void createSpot(ParkingLot lot, String code, SpotType type, boolean hasCharger) {
        ParkingSpot spot = ParkingSpot.builder()
                .spotCode(code)
                .occupied(false)
                .parkingLot(lot)
                .type(type)
                .hasCharger(hasCharger)
                .build();
        parkingSpotRepository.save(spot);
    }
}