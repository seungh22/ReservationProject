package com.example.reservation.controller;

import com.example.reservation.domain.model.KioskRequest;
import com.example.reservation.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kiosk")
public class KioskController {
    private final KioskService kioskService;

    /**
     * 키오스크 방문 확인
     */
    @PatchMapping("/confirm/{reservationId}")
    public ResponseEntity<?> confirmVisit(@PathVariable Long reservationId,
                                          @RequestBody @Valid KioskRequest kioskRequest) {
        return ResponseEntity.ok(kioskService.confirmVisit(reservationId, kioskRequest));
    }

}
