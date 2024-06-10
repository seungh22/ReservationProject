package com.example.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류"),
    INVALID_REQUEST("잘못된 요청입니다."),
    ACCESS_DENIED("권한이 없습니다."),

    // 사용자 관련
    NOT_FOUND_MEMBER("사용자 정보가 없습니다."),
    PASSWORD_UNMATCH("비밀번호가 일치하지 않습니다."),
    ALREADY_USING_ID("이미 사용중인 아이디 입니다."),
    NEED_LOGIN("로그인 후 이용하실 수 있습니다."),
    CANNOT_DELETE_OTHER_MEMBER("회원 탈퇴는 본인 계정만 할 수 있습니다."),
    ONLY_FOR_USER("사용자 권한 필요."),
    ONLY_FOR_PARTNER("파트너 권한 필요."),
    MEMBER_HAS_STORE("매장이 있는 사용자는 탈퇴할 수 없습니다."),

    // 매장 관련
    NOT_FOUND_STORE("매장 정보가 없습니다."),
    ALREADY_EXISTS_STORE("이미 등록되어 있는 매장입니다."),
    SERVICE_ONLY_FOR_OWNER("해당 매장의 점장만 이용가능한 서비스입니다."),
    STORE_HAS_RESERVATION("예약이 있는 매장을 삭제하실 수 없습니다."),

    // 예약 관련
    NOT_FOUND_RESERVATION("예약 정보가 없습니다."),
    UNMATCH_RESERVATION_USER("해당 사용자의 예약이 아닙니다."),
    CANNOT_RESERVE_PAST_DATE("예약 시간이 현재 시간보다 과거입니다."),
    ALREADY_RESERVED_TIME("이미 예약이 된 시간입니다. 다른 시간을 선택해주세요."),
    NOT_APPROVED_RESERVATION("승인된 예약이 아닙니다."),
    UNMATCH_RESERVED_INFORMATION("예약 정보 불일치"),
    ARRIVE_TOO_EARLY("아직 예약시간 10분전이 아닙니다."),
    ARRIVE_TOO_LATE("예약시간 10분전이 지났습니다."),
    CANNOT_UPDATE_RESERVATION("예약시간 30분전 이후부터는 예약 변경이 불가능 합니다."),
    CANNOT_CANCEL_RESERVATION("예약시간 30분전 이후부터는 예약 취소가 불가능 합니다."),
    ALREADY_VISITED_RESERVATION("이미 방문처리된 예약입니다."),

    // 리뷰 관련
    NOT_FOUND_REVIEW("리뷰 정보가 없습니다."),
    UNMATCH_REVIEW_USER("해당 사용자의 리뷰가 아닙니다."),
    NOT_USED_RESERVATION("예약 및 사용하지 않은 매장에 리뷰를 작성할 수 없습니다."),
    ALREADY_REVIEWED_RESERVATION("이미 리뷰를 작성한 예약 내역입니다.");

    private final String description;
}
