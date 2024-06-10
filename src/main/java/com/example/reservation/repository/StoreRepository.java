package com.example.reservation.repository;

import com.example.reservation.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByAddressAndContact(String address, String contact);

    List<Store> findByNameStartsWith(@Param("name") String name);

    Page<Store> findAllByOrderByName(Pageable pageable);

    List<Store> findByOwner(String owner);

    Page<Store> findAllByOrderByRatingDesc(Pageable pageable);

    @Query("SELECT s FROM Store s LEFT JOIN s.reviews r GROUP BY s.id ORDER BY COUNT(r) DESC")
    Page<Store> findAllByOrderByReviewCountDesc(Pageable pageable);

}
