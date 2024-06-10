package com.example.reservation.domain.model;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {
    private String content;

    @Min(0)
    @Max(5)
    private Double rating;
}
