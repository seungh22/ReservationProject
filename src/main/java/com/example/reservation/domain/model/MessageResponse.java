package com.example.reservation.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 단순 메시지를 리턴할 때 사용할 DTO
public class MessageResponse {
    private String message;
}
