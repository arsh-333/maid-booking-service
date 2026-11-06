package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.domain.model.Maid;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeFilter implements MaidFilter {

    @Override
    public boolean matches(Maid maid, SearchContext ctx) {
        return ctx.getServiceType() == null
                || maid.offers(ctx.getServiceType());
    }
}
