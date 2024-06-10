package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.model.KioskRequest;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.service.KioskService;
import com.example.reservation.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 10분전에 도착하여 키오스크를 통해서 방문확인을 진행
     * 키오스크 화면에는 사용자 아이디, 이름, 휴대폰 번호를 입력하도록 하고
     * 입력받은 값들을 가지고 예약 확인을 진행한다.
     */
    @Override
    public MessageResponse confirmVisit(Long reservationId, KioskRequest kioskRequest) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        validateReservation(kioskRequest, reservation);

        reservation.updateVisitYn(true);
        reservationRepository.save(reservation);

        return MessageResponse.builder()
                .message("예약 확인이 완료되었습니다.")
                .build();
    }

    /**
     * 방문확인 유효성 검증
     */
    private static void validateReservation(KioskRequest kioskRequest, Reservation reservation) {

        // 예약자 아이디, 예약자 이름, 예약자 핸드폰 번호를 입력받은 값과 비교
        if (!Objects.equals(reservation.getMember().getUserId(), kioskRequest.getUserId())
                || !Objects.equals(reservation.getMember().getName(), kioskRequest.getName())
                || !Objects.equals(reservation.getMember().getPhone(), kioskRequest.getPhone())) {
            throw new ReservationException(ErrorCode.UNMATCH_RESERVED_INFORMATION);
        }

        // 예약상태가 승인상태가 아닌 경우 예외 발생
        if (reservation.getReservationStatus() != ReservationStatus.APPROVAL) {
            throw new ReservationException(ErrorCode.NOT_APPROVED_RESERVATION);
        }

        // 이미 방문처리된 예약인 경우 예외 발생
        if (reservation.isVisitYn()) {
            throw new ReservationException(ErrorCode.ALREADY_VISITED_RESERVATION);
        }

        // 현재 시간을 HH:mm:00 형태로 변환
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        // 예약시간 10분 전보다 일찍 도착했다면 예외 발생
        if (now.isBefore(reservation.getReservationDate().minusMinutes(10))) {
            throw new ReservationException(ErrorCode.ARRIVE_TOO_EARLY);
        }

        // 예약시간 10분 전보다 늦게 도착했다면 예외 발생
        if (now.plusMinutes(10).isAfter(reservation.getReservationDate())) {
            throw new ReservationException(ErrorCode.ARRIVE_TOO_LATE);
        }
    }
}
