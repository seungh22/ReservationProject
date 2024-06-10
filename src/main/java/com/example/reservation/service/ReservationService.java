package com.example.reservation.service;

import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.ReservationPartnerResponse;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationService {
    ReservationResponse reserveStore(Long id, ReservationRequest reservationRequest);

    ReservationResponse updateReservation(Long reservationId, ReservationRequest reservationRequest);

    MessageResponse cancelReservation(Long reservationId);

    Page<ReservationResponse> getReservationListForUser(Pageable pageable);

    Page<ReservationPartnerResponse> getReservationListForPartner(Long storeId, LocalDate date, Pageable pageable);

    ReservationPartnerResponse approveReservation(Long reservationId);

    ReservationPartnerResponse refuseReservation(Long reservationId);

}
