package com.example.reservation.service;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MemberService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    SignUpResponse signUp(SignUpRequest signUpRequest);

    Member authenticate(SignInRequest signInRequest);

    MessageResponse deleteMember(Long memberId);
}
