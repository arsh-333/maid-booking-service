package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.availability.AvailabilityService;
import com.rupeek.maidbooking.domain.model.Maid;
import org.springframework.stereotype.Component;

/** Returns only maids actually free for the requested slot (requirement 3.1). */
@Component
public class AvailabilityFilter implements MaidFilter {

    private final AvailabilityService availabilityService;

    public AvailabilityFilter(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @Override
    public boolean matches(Maid maid, SearchContext ctx) {
        return ctx.getSlot() == null
                || availabilityService.isAvailable(maid, ctx.getSlot());
    }
}
