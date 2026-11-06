package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.domain.model.Maid;
import org.springframework.stereotype.Component;

/** Rating for the requested service (or average across services) must be >= minRating. */
@Component
public class MinRatingFilter implements MaidFilter {

    @Override
    public boolean matches(Maid maid, SearchContext ctx) {
        if (ctx.getMinRating() == null) {
            return true;
        }
        if (ctx.getServiceType() != null) {
            Double rating = maid.getRatings().get(ctx.getServiceType());
            return rating != null && rating >= ctx.getMinRating();
        }
        return maid.getRatings().values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0) >= ctx.getMinRating();
    }
}
