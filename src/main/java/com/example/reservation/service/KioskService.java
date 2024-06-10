package com.example.reservation.service;

import com.example.reservation.domain.model.KioskRequest;
import com.example.reservation.domain.model.MessageResponse;

public interface KioskService {
    MessageResponse confirmVisit(Long reservationId, KioskRequest kioskRequest);
}
