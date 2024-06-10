package com.example.reservation.controller;

import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 매장 예약
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<?> reserveStore(@PathVariable Long storeId,
                                          @RequestBody @Valid ReservationRequest reservationRequest) {

        return ResponseEntity.ok(reservationService.reserveStore(storeId, reservationRequest));
    }

    /**
     * 예약 수정
     * 예약 리스트에서 예약 내역을 보고 수정을 하도록 한다.
     */
    @PatchMapping("/{reservationId}")
    public ResponseEntity<?> updateReservation(@PathVariable Long reservationId,
                                               @RequestBody @Valid ReservationRequest reservationRequest) {
        return ResponseEntity.ok(reservationService.updateReservation(reservationId, reservationRequest));
    }

    /**
     * 예약 취소
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    /**
     * 예약 확인 - 사용자
     * 자기가 예약한 예약 내역을 리스트로 보여준다.
     * 로그인한 사용자가 유저 권한을 가지고 있어야 함.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getReservationListForUser(Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationListForUser(pageable));
    }

    /**
     * 예약 확인 - 파트너
     * 자기가 관리하는 매장의 예약 현황을 리스트로 보여준다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @GetMapping("/list/{storeId}")
    public ResponseEntity<?> getReservationListForPartner(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                          @PathVariable Long storeId,
                                                          Pageable pageable) {
        return ResponseEntity.ok(reservationService.getReservationListForPartner(storeId, date, pageable));
    }

    /**
     * 예약 승인
     * 점장이 예약내역을 보고 승인이 가능한 시간대라면 예약을 승인한다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @PatchMapping("/approval/{reservationId}")
    public ResponseEntity<?> approveReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.approveReservation(reservationId));
    }

    /**
     * 예약 거절
     * 점장이 예약내역을 보고 승인이 불가능한 시간대라면 예약을 거절한다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @PatchMapping("/refusal/{reservationId}")
    public ResponseEntity<?> refuseReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.refuseReservation(reservationId));
    }

}
