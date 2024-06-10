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
public class ReservationResponse {
    private String storeName;
    private String memberName;
    private String address;
    private String contact;
    private LocalDateTime reservationDate;
    private ReservationStatus reservationStatus;

    public static Page<ReservationResponse> toDtoList(Page<Reservation> page) {
        return page.map(reservation -> ReservationResponse.builder()
                .storeName(reservation.getStore().getName())
                .memberName(reservation.getMember().getName())
                .address(reservation.getStore().getAddress())
                .contact(reservation.getStore().getContact())
                .reservationDate(reservation.getReservationDate())
                .reservationStatus(reservation.getReservationStatus())
                .build());

    }

}
