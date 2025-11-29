package com.akilliotopark.service;

import com.akilliotopark.entity.ParkingLot;
import com.akilliotopark.entity.Subscription;
import com.akilliotopark.entity.User;
import com.akilliotopark.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public boolean hasActiveSubscription(User user, ParkingLot parkingLot) {
        List<Subscription> subs = subscriptionRepository.findByUserIdAndActiveTrue(user.getId());

        return subs.stream()
                .anyMatch(sub ->
                        sub.getParkingLot().getId().equals(parkingLot.getId()) &&
                                sub.getEndDate().isAfter(LocalDate.now())
                );
    }
}