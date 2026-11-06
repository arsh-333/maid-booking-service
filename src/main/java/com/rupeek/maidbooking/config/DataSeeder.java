package com.rupeek.maidbooking.config;

import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.AvailabilityWindow;
import com.rupeek.maidbooking.repository.MaidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Demo data so the core flows can be exercised right after startup. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MaidRepository maidRepository;

    public DataSeeder(MaidRepository maidRepository) {
        this.maidRepository = maidRepository;
    }

    @Override
    public void run(String... args) {
        Maid sunita = maidRepository.save(new Maid(UUID.randomUUID().toString(), "Sunita",
                Gender.FEMALE, "Koramangala",
                Map.of(ServiceType.CLEANING, new BigDecimal("300"), ServiceType.COOKING, new BigDecimal("400")),
                List.of(new AvailabilityWindow(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                        new AvailabilityWindow(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                        new AvailabilityWindow(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                        new AvailabilityWindow(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                        new AvailabilityWindow(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))),
                Map.of(ServiceType.CLEANING, 4.5, ServiceType.COOKING, 4.0)));

        Maid raju = maidRepository.save(new Maid(UUID.randomUUID().toString(), "Raju",
                Gender.MALE, "HSR Layout",
                Map.of(ServiceType.DISHWASHING, new BigDecimal("200")),
                List.of(new AvailabilityWindow(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)),
                        new AvailabilityWindow(DayOfWeek.SUNDAY, LocalTime.of(9, 0), LocalTime.of(13, 0))),
                Map.of(ServiceType.DISHWASHING, 4.2)));

        log.info("Seeded maidIds=[Sunita={}, Raju={}]", sunita.getId(), raju.getId());
    }
}
