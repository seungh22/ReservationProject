package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.MemberService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.reservation.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserId(username)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));
    }

    /**
     * 정보를 입력받아 회원가입 진행
     */
    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        boolean exists = memberRepository.existsByUserId(signUpRequest.getUserId());

        // 입력받은 아이디가 이미 존재하면 예외 발생
        if (exists) {
            throw new ReservationException(ErrorCode.ALREADY_USING_ID);
        }

        // 비밀번호는 암호화해서 저장
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        memberRepository.save(signUpRequest.toEntity());

        return SignUpResponse.builder()
                .userId(signUpRequest.getUserId())
                .password(signUpRequest.getPassword())
                .name(signUpRequest.getName())
                .phone(signUpRequest.getPhone())
                .memberType(signUpRequest.getMemberType())
                .build();
    }

    /**
     * 로그인
     * 입력받은 아이디와 비밀번호를 통해 사용자를 리턴한다.
     */
    @Override
    public Member authenticate(SignInRequest signInRequest) {
        Member member = memberRepository.findByUserId(signInRequest.getUserId())
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(signInRequest.getPassword(), member.getPassword())) {
            throw new ReservationException(PASSWORD_UNMATCH);
        }

        return member;
    }

    /**
     * 회원 탈퇴
     * 상점의 점장인 경우 상점을 먼저 삭제해야 탈퇴할 수 있다.
     */
    @Override
    public MessageResponse deleteMember(Long memberId) {
        String userId = LoginCheckUtils.getUserId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));

        // 로그인한 사용자와 탈퇴하려는 사용자의 아이디가 같지 않으면 예외 발생
        if (!member.getUserId().equals(userId)) {
            throw new ReservationException(CANNOT_DELETE_OTHER_MEMBER);
        }

        List<Store> storeList = storeRepository.findByOwner(member.getUserId());

        // 매장이 존재하는 사용자는 탈퇴 불가 -> 예외 발생
        if (!storeList.isEmpty()) {
            throw new ReservationException(MEMBER_HAS_STORE);
        }

        memberRepository.delete(member);

        return MessageResponse.builder()
                .message("회원 탈퇴 완료!")
                .build();
    }
}
