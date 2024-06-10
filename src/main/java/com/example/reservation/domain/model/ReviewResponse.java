package com.example.reservation.domain.model;

import com.example.reservation.domain.entity.Review;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private String userId;
    private String storeName;
    private String content;
    private Double rating;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .userId(review.getMember().getUserId())
                .storeName(review.getStore().getName())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
    }
}
