package com.example.reservation.domain.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KioskRequest {
    @NotBlank
    private String userId;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
