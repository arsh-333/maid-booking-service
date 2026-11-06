package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.domain.model.Maid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Price of the requested service (or the maid's cheapest service) must be <= maxPrice. */
@Component
public class PriceRangeFilter implements MaidFilter {

    @Override
    public boolean matches(Maid maid, SearchContext ctx) {
        if (ctx.getMaxPrice() == null) {
            return true;
        }
        if (ctx.getServiceType() != null) {
            BigDecimal price = maid.getPrices().get(ctx.getServiceType());
            return price != null && price.compareTo(ctx.getMaxPrice()) <= 0;
        }
        return maid.getPrices().values().stream()
                .anyMatch(p -> p.compareTo(ctx.getMaxPrice()) <= 0);
    }
}
