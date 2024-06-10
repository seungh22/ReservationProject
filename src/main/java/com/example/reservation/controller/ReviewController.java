package com.example.reservation.controller;

import com.example.reservation.domain.model.ReviewRequest;
import com.example.reservation.domain.model.ReviewUpdateRequest;
import com.example.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 예약내역에 대한 리뷰를 작성
     * 로그인하지 않은 경우 리뷰를 작성할 수 없음.
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<?> addReview(@PathVariable Long reservationId,
                                       @RequestBody @Valid ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.addReview(reservationId, reviewRequest));
    }

    /**
     * 리뷰 수정
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewUpdateRequest));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }

    /**
     * 매장에 대한 리뷰 리스트를 보여준다.
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<?> showReviews(@PathVariable Long storeId) {
        return ResponseEntity.ok(reviewService.showReviews(storeId));
    }
}
