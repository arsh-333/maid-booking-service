package com.rupeek.maidbooking.repository;

import com.rupeek.maidbooking.domain.model.Maid;

import java.util.List;
import java.util.Optional;

public interface MaidRepository {

    Maid save(Maid maid);

    Optional<Maid> findById(String id);

    List<Maid> findAll();
}
