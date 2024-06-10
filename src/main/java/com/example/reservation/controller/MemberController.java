package com.example.reservation.controller;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import com.example.reservation.security.TokenProvider;
import com.example.reservation.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = memberService.signUp(signUpRequest);
        return ResponseEntity.ok(signUpResponse);
    }

    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        Member member = memberService.authenticate(signInRequest);

        String token = tokenProvider.generateToken(member.getUserId(), member.getMemberType());
        log.info("{} 사용자 로그인 성공", signInRequest.getUserId());

        return ResponseEntity.ok(token);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.deleteMember(memberId));
    }

}
