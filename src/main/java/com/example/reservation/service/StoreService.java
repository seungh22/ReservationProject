package com.example.reservation.service;

import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.StoreRequest;
import com.example.reservation.domain.model.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {
    StoreResponse addStore(StoreRequest storeRequest);

    StoreResponse modifyStore(StoreRequest storeRequest, Long storeId);

    MessageResponse deleteStore(Long storeId);

    Page<StoreResponse> getStoresOrderByName(Pageable pageable);

    Page<StoreResponse> getStoresOrderByRating(Pageable pageable);

    Page<StoreResponse> getStoresOrderByReviewCount(Pageable pageable);

    List<StoreResponse> searchStore(String prefix);

    StoreResponse getStoreDetails(Long id);


}
