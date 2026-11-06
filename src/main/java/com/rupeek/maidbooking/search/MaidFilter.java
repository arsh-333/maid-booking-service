package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.domain.model.Maid;

/**
 * Extension point for discovery. Each filter self-skips when its criterion
 * is absent from the SearchContext. A new filter = a new @Component implementing
 * this interface; MaidSearchService picks it up automatically.
 */
public interface MaidFilter {

    boolean matches(Maid maid, SearchContext ctx);
}
