package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.StoreRequest;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.StoreService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    /**
     * 매장 등록
     * userId 파라미터는 매장을 등록할 때 점장을 등록하기 위함
     * 로그인된 파트너 권한을 가진 사용자의 아이디를 사용
     */
    @Override
    public StoreResponse addStore(StoreRequest storeRequest) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        // 파트너 권한이 없는 경우
        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        // 주소와 연락처가 같으면 같은 매장이라고 판단
        Optional<Store> optionalStore = storeRepository.findByAddressAndContact(storeRequest.getAddress(), storeRequest.getContact());

        // 같은 매장이 존재하면 예외 발생 (동일한 매장 중복 등록 불가능)
        if (optionalStore.isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXISTS_STORE);
        }

        Store store = Store.builder()
                .owner(userId)
                .name(storeRequest.getName())
                .address(storeRequest.getAddress())
                .description(storeRequest.getDescription())
                .contact(storeRequest.getContact())
                .open(storeRequest.getOpen())
                .close(storeRequest.getClose())
                .build();

        Store savedStore = storeRepository.save(store);

        return StoreResponse.of(savedStore);
    }

    /**
     * 매장 정보 수정
     * 로그인 된 사용자 아이디와 매장의 점장을 비교해서 사용자 소유 매장일 경우 정보 변경
     */
    @Override
    public StoreResponse modifyStore(StoreRequest storeRequest, Long storeId) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        // 파트너 권한이 없는 경우
        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        // 점장과 로그인 한 사용자 아이디를 비교, 다르면 소유한 매장이 아니므로 예외 발생
        validateOwnerAndUser(store.getOwner(), userId);

        // 매장 정보 수정
        store.updateStore(storeRequest);

        // 수정된 정보로 매장을 찾음
        Optional<Store> optionalStore = storeRepository.findByAddressAndContact(store.getAddress(), store.getContact());

        // 같은 매장이 존재하면 예외 발생 (동일한 매장 중복 등록 불가능)
        if (optionalStore.isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXISTS_STORE);
        }

        Store savedStore = storeRepository.save(store);

        return StoreResponse.of(savedStore);
    }

    /**
     * 매장 삭제
     */
    @Override
    public MessageResponse deleteStore(Long storeId) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        // 삭제하려는 매장이 예약이 있다면 삭제 불가
        hasReservation(store);

        validateOwnerAndUser(store.getOwner(), userId);

        storeRepository.delete(store);

        return MessageResponse.builder()
                .message("매장 삭제 완료!")
                .build();
    }

    // 매장의 예약 리스트가 비어있지 않으면 예외 발생
    private static void hasReservation(Store store) {
        if (!store.getReservationList().isEmpty()) {
            throw new ReservationException(STORE_HAS_RESERVATION);
        }
    }

    // 점장과 로그인한 사용자 정보가 다르면 예외 발생
    private void validateOwnerAndUser(String ownerId, String userId) {
        if (!ownerId.equals(userId)) {
            throw new ReservationException(SERVICE_ONLY_FOR_OWNER);
        }
    }

    /**
     * 매장을 이름 순으로 리스트업
     */
    @Override
    public Page<StoreResponse> getStoresOrderByName(Pageable pageable) {
        Page<Store> orderByName = storeRepository.findAllByOrderByName(pageable);

        return StoreResponse.toDtoList(orderByName);
    }

    /**
     * 매장을 평점 순으로 리스트업
     */
    @Override
    public Page<StoreResponse> getStoresOrderByRating(Pageable pageable) {
        Page<Store> orderByRating = storeRepository.findAllByOrderByRatingDesc(pageable);

        return StoreResponse.toDtoList(orderByRating);
    }

    /**
     * 매장을 리뷰 개수가 많은 순으로 리스트업
     */
    @Override
    public Page<StoreResponse> getStoresOrderByReviewCount(Pageable pageable) {
        Page<Store> orderByReviewCount = storeRepository.findAllByOrderByReviewCountDesc(pageable);

        return StoreResponse.toDtoList(orderByReviewCount);
    }

    /**
     * 매장 검색
     */
    @Override
    public List<StoreResponse> searchStore(String name) {
        List<Store> storeList = storeRepository.findByNameStartsWith(name);

        return storeList.stream().map(StoreResponse::of).collect(Collectors.toList());
    }

    /**
     * 매장 상세 정보 확인
     */
    @Override
    public StoreResponse getStoreDetails(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        return StoreResponse.of(store);
    }


}
