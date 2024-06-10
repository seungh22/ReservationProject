package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.ReservationPartnerResponse;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.ReservationService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.*;
import static com.example.reservation.type.ReservationStatus.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    /**
     * 매장 예약
     * 로그인하지 않은 사용자는 예약이 불가능 (회원가입 필수)
     */
    @Override
    public ReservationResponse reserveStore(Long id, ReservationRequest reservationRequest) {
        String userId = LoginCheckUtils.getUserId();
        List<String> authorities = LoginCheckUtils.getAuthorities();

        // 사용자 권한이 없는 경우 예외 발생
        if (!authorities.contains("ROLE_USER")) {
            throw new ReservationException(ONLY_FOR_USER);
        }

        // 예약 시간이 현재시간 이전이라면 예외 발생
        checkReservationDate(reservationRequest);

        Member member = getMember(userId);
        Store store = getStore(id);

        Optional<Reservation> reservationOptional
                = reservationRepository.findByStoreAndReservationDate(store, reservationRequest.getReservationDate());

        // 한 매장의 같은 시간에 예약이 있다면 예외 발생
        // 우선적으로 동일한 시간에 대해서만 예외 처리를 함
        // 어차피 점장이 예약 승인, 거절을 할 수 있기 때문에 동일한 시간을 제외하고는 점장 판단하에 처리하도록
        if (reservationOptional.isPresent()) {
            throw new ReservationException(ALREADY_RESERVED_TIME);
        }

        reservationRepository.save(Reservation.builder()
                .member(member)
                .store(store)
                .reservationDate(reservationRequest.getReservationDate())
                .reservationStatus(WAITING)
                .build());

        return ReservationResponse.builder()
                .storeName(store.getName())
                .memberName(member.getName())
                .address(store.getAddress())
                .contact(store.getContact())
                .reservationDate(reservationRequest.getReservationDate())
                .reservationStatus(WAITING)
                .build();
    }

    /**
     * 예약 수정
     * 정책상 예약시간 30분전 이후부터는 예약 변경이 불가능 하도록 한다.
     */
    @Override
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest reservationRequest) {
        String userId = LoginCheckUtils.getUserId();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        // 예약자와 로그인한 사용자가 같지 않은 경우 예외 발생
        if (!Objects.equals(reservation.getMember().getUserId(), userId)) {
            throw new ReservationException(UNMATCH_RESERVATION_USER);
        }

        checkReservationDate(reservationRequest);

        LocalDateTime now = LocalDateTime.now();

        // 정책상 이미 방문처리 된 상태거나 (= 예약시간이 이미 10분밖에 안남았다는 것을 의미)
        // 예약 시간 30분전부터는 예약 변경 불가능
        if (reservation.isVisitYn() || now.isAfter(reservation.getReservationDate().minusMinutes(30))) {
            throw new ReservationException(CANNOT_UPDATE_RESERVATION);
        }

        Optional<Reservation> reservationOptional
                = reservationRepository.findByStoreAndReservationDate(reservation.getStore(), reservationRequest.getReservationDate());

        // 한 매장의 같은 시간에 예약이 있다면 예외 발생
        if (reservationOptional.isPresent()) {
            throw new ReservationException(ALREADY_RESERVED_TIME);
        }

        // 변경된 시간으로 예약을 변경하고 상태를 다시 대기상태로 바꾼다.
        reservation.updateReservation(reservationRequest.getReservationDate());

        reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .storeName(reservation.getStore().getName())
                .memberName(reservation.getMember().getName())
                .address(reservation.getStore().getAddress())
                .contact(reservation.getStore().getContact())
                .reservationDate(reservationRequest.getReservationDate())
                .reservationStatus(WAITING)
                .build();
    }


    /**
     * 매장 예약 취소
     * 정책상 예약시간 30분전 이후부터는 취소가 불가능 하도록 한다.
     */
    @Override
    public MessageResponse cancelReservation(Long reservationId) {
        String userId = LoginCheckUtils.getUserId();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        // 예약자와 로그인한 사용자가 같지 않은 경우 예외 발생
        if (!Objects.equals(reservation.getMember().getUserId(), userId)) {
            throw new ReservationException(UNMATCH_RESERVATION_USER);
        }

        LocalDateTime now = LocalDateTime.now();

        // 정책상 이미 방문처리 된 상태거나 (= 예약시간이 이미 10분밖에 안남았다는 것을 의미)
        // 예약 시간 30분전부터는 취소가 불가능
        if (reservation.isVisitYn() || now.isAfter(reservation.getReservationDate().minusMinutes(30))) {
            throw new ReservationException(CANNOT_CANCEL_RESERVATION);
        }

        reservationRepository.delete(reservation);

        return MessageResponse.builder()
                .message("예약 취소 완료!")
                .build();
    }


    /**
     * 예약 내역 - 사용자
     */
    @Override
    public Page<ReservationResponse> getReservationListForUser(Pageable pageable) {
        String userId = LoginCheckUtils.getUserId();
        List<String> authorities = LoginCheckUtils.getAuthorities();

        // 사용자 권한이 없으면 예외 발생
        if (!authorities.contains("ROLE_USER")) {
            throw new ReservationException(ONLY_FOR_USER);
        }

        Member member = getMember(userId);

        // 사용자가 예약한 예약 내역을 최근에 예약한것부터 리스트로 보여준다.
        Page<Reservation> memberReservationPage = reservationRepository.findAllByMemberOrderByReservationDateDesc(member, pageable);

        return ReservationResponse.toDtoList(memberReservationPage);
    }

    /**
     * 예약 내역 - 파트너
     */
    @Override
    public Page<ReservationPartnerResponse> getReservationListForPartner(Long storeId, LocalDate date, Pageable pageable) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        // 파트너 권한이 없으면 예외 발생
        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        Store store = getStore(storeId);
        Member member = getMember(userId);

        // 매장의 점장과, 로그인한 사용자의 아이디가 같아야 해당 매장의 점장임을 확인할 수 있음
        // 다르다면 예외 발생
        if (!Objects.equals(store.getOwner(), member.getUserId())) {
            throw new ReservationException(SERVICE_ONLY_FOR_OWNER);
        }

        Page<Reservation> storeReservationPage = reservationRepository.findAllByStoreOrderByReservationDate(store, pageable);

        // 입력받은 날짜를 통해 예약 내역에서 동일한 날짜를 가지는 예약 내역만 리스트로 가져온다.
        List<Reservation> filteredReservations = storeReservationPage.stream().filter(
                srp -> srp.getReservationDate().getYear() == date.getYear() &&
                        srp.getReservationDate().getMonth() == date.getMonth() &&
                        srp.getReservationDate().getDayOfMonth() == date.getDayOfMonth()).collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());

        return ReservationPartnerResponse.toDtoList(filteredPage);
    }

    /**
     * 예약 승인
     */
    @Override
    public ReservationPartnerResponse approveReservation(Long reservationId) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        // 파트너 권한이 없는 경우
        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        Member member = getMember(userId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        // 예약된 매장의 점장과 현재 로그인한 사용자의 아이디가 일치하지 않으면 권한이 없기 때문에 예외 발생
        if (!Objects.equals(reservation.getStore().getOwner(), member.getUserId())) {
            throw new ReservationException(SERVICE_ONLY_FOR_OWNER);
        }

        // 예약 상태를 승인 상태로 변경 후 저장
        reservation.updateStatus(APPROVAL);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationPartnerResponse.builder()
                .memberName(savedReservation.getMember().getName())
                .phone(savedReservation.getMember().getPhone())
                .reservationDate(savedReservation.getReservationDate())
                .reservationStatus(savedReservation.getReservationStatus())
                .build();
    }

    /**
     * 예약 거절
     */
    @Override
    public ReservationPartnerResponse refuseReservation(Long reservationId) {
        List<String> authorities = LoginCheckUtils.getAuthorities();
        String userId = LoginCheckUtils.getUserId();

        // 파트너 권한이 없는 경우
        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        Member member = getMember(userId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION));

        // 예약된 매장의 점장과 로그인한 사용자의 아이디가 일치하지 않으면 권한이 없기 때문에 예외 발생
        if (!Objects.equals(reservation.getStore().getOwner(), member.getUserId())) {
            throw new ReservationException(SERVICE_ONLY_FOR_OWNER);
        }

        // 예약 상태를 거절 상태로 변경 후 저장
        reservation.updateStatus(REFUSAL);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationPartnerResponse.builder()
                .memberName(savedReservation.getMember().getName())
                .phone(savedReservation.getMember().getPhone())
                .reservationDate(savedReservation.getReservationDate())
                .reservationStatus(savedReservation.getReservationStatus())
                .build();
    }


    private Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));
    }

    private static void checkReservationDate(ReservationRequest reservationRequest) {
        if (reservationRequest.getReservationDate().isBefore(LocalDateTime.now())) {
            throw new ReservationException(CANNOT_RESERVE_PAST_DATE);
        }
    }
}
