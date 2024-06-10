package com.example.reservation.repository;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByStoreAndReservationDate(Store store, LocalDateTime reservationDate);

    Page<Reservation> findAllByMemberOrderByReservationDateDesc(Member member, Pageable pageable);

    Page<Reservation> findAllByStoreOrderByReservationDate(Store store, Pageable pageable);

    Optional<Reservation> findByStoreAndMember(Store store, Member member);
}
