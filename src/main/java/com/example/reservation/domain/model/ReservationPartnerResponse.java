package com.example.reservation.domain.model;

import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.type.ReservationStatus;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationPartnerResponse {
    private String memberName;
    private String phone;
    private LocalDateTime reservationDate;
    private ReservationStatus reservationStatus;

    public static Page<ReservationPartnerResponse> toDtoList(Page<Reservation> page) {
        return page.map(reservation -> ReservationPartnerResponse.builder()
                .memberName(reservation.getMember().getName())
                .phone(reservation.getMember().getPhone())
                .reservationDate(reservation.getReservationDate())
                .reservationStatus(reservation.getReservationStatus())
                .build());

    }
}
