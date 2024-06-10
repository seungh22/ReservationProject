package com.example.reservation.domain.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private String description;

    @NotBlank
    private String contact;

    @NotNull
    private LocalTime open;

    @NotNull
    private LocalTime close;
}
