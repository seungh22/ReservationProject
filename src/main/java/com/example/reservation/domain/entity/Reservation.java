package com.example.reservation.domain.entity;

import com.example.reservation.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private boolean visitYn;

    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    public void updateReservation(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
        this.reservationStatus = ReservationStatus.WAITING;
    }

    public void updateStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public void updateVisitYn(boolean visit) {
        this.visitYn = visit;
    }
}
