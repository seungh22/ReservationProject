package com.example.reservation.domain.model;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.type.MemberType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank
    private String userId;

    @NotBlank
    @Size(min = 4)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    private MemberType memberType;

    public Member toEntity() {
        return Member.builder()
                .userId(this.userId)
                .password(this.password)
                .name(this.name)
                .phone(this.phone)
                .memberType(this.memberType)
                .build();
    }
}
