package com.rupeek.maidbooking.search;

import com.rupeek.maidbooking.dto.SearchContext;
import com.rupeek.maidbooking.domain.model.Maid;
import com.rupeek.maidbooking.repository.MaidRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Applies every registered MaidFilter with AND semantics.
 * Spring injects all filter beans — adding a filter never touches this class.
 */
@Service
public class MaidSearchService {

    private final MaidRepository maidRepository;
    private final List<MaidFilter> filters;

    public MaidSearchService(MaidRepository maidRepository, List<MaidFilter> filters) {
        this.maidRepository = maidRepository;
        this.filters = filters;
    }

    public List<Maid> search(SearchContext ctx) {
        return maidRepository.findAll().stream()
                .filter(maid -> filters.stream().allMatch(f -> f.matches(maid, ctx)))
                .toList();
    }
}
