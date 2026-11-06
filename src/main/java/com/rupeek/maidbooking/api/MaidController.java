package com.rupeek.maidbooking.api;

import com.rupeek.maidbooking.dto.CreateMaidRequest;
import com.rupeek.maidbooking.domain.enums.Gender;
import com.rupeek.maidbooking.domain.enums.ServiceType;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.domain.vo.TimeSlot;
import com.rupeek.maidbooking.repository.MaidRepository;
import com.rupeek.maidbooking.search.MaidSearchService;
import com.rupeek.maidbooking.dto.SearchContext;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/maids")
public class MaidController {

    private final MaidRepository maidRepository;
    private final MaidSearchService searchService;

    public MaidController(MaidRepository maidRepository, MaidSearchService searchService) {
        this.maidRepository = maidRepository;
        this.searchService = searchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Maid onboard(@Valid @RequestBody CreateMaidRequest request) {
        Maid maid = new Maid(UUID.randomUUID().toString(), request.getName(), request.getGender(),
                request.getLocality(), request.getPrices(), request.getAvailability(),
                request.getRatings() == null ? Map.of() : request.getRatings());
        return maidRepository.save(maid);
    }

    @GetMapping("/search")
    public List<Maid> search(@RequestParam(required = false) String locality,
                             @RequestParam(required = false) ServiceType service,
                             @RequestParam(required = false) Gender gender,
                             @RequestParam(required = false) BigDecimal maxPrice,
                             @RequestParam(required = false) Double minRating,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end) {
        TimeSlot slot = null;
        if (date != null && start != null && end != null) {
            slot = new TimeSlot(date, start, end);
        }
        return searchService.search(new SearchContext(locality, service, gender, maxPrice, minRating, slot));
    }
}
