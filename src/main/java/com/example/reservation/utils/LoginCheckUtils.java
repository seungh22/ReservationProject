package com.example.reservation.utils;

import com.example.reservation.exception.ReservationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.NEED_LOGIN;

public class LoginCheckUtils {

    // jwt 토큰을 통해 로그인한 사용자의 아이디를 리턴해준다.
    public static String getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new ReservationException(NEED_LOGIN);
        }

        return ((UserDetails) principal).getUsername();
    }

    // 사용자 권한을 리스트로 반환한다.
    public static List<String> getAuthorities() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new ReservationException(NEED_LOGIN);
        }

        return ((UserDetails) principal).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

    }
}
