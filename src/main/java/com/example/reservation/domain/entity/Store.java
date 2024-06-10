package com.example.reservation.domain.entity;

import com.example.reservation.domain.model.StoreRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;
    private String name;
    private String address;
    private String description;
    private String contact;
    private LocalTime open;
    private LocalTime close;

    @Builder.Default
    private Double rating = 0.0;

    @OneToMany(mappedBy = "store")
    private List<Reservation> reservationList;

    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateStore(StoreRequest storeRequest) {
        this.name = storeRequest.getName();
        this.address = storeRequest.getAddress();
        this.description = storeRequest.getDescription();
        this.contact = storeRequest.getContact();
        this.open = storeRequest.getOpen();
        this.close = storeRequest.getClose();
    }

    public void updateRating() {
        double totalRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .sum();
        int totalCount = reviews.size();

        System.out.println("totalRating = " + totalRating);
        System.out.println("totalCount = " + totalCount);

        this.rating = (double) Math.round(totalRating / totalCount * 10) / 10;

        System.out.println("this.rating = " + this.rating);
    }
}
