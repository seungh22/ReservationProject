package com.example.reservation.domain.model;

import com.example.reservation.type.MemberType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {
    private String userId;
    private String password;
    private String name;
    private String phone;

    private MemberType memberType;
}
