package com.doubleowner.revibe.domain.review.entity;

import com.doubleowner.revibe.domain.item.entity.Item;
import com.doubleowner.revibe.domain.payment.entity.Payment;
import com.doubleowner.revibe.domain.review.dto.ReviewResponseDto;
import com.doubleowner.revibe.domain.review.dto.UpdateReviewRequestDto;
import com.doubleowner.revibe.domain.user.entity.User;
import com.doubleowner.revibe.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer starRate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String reviewImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    /**
     * DTO로 변경해주는 메소드
     *
     * @param review
     * @return
     */
    public static ReviewResponseDto toDto(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .starRate(review.getStarRate())
                .createdAt(review.getCreatedAt())
                .image(review.getReviewImage())
                .build();
    }

    public void update(UpdateReviewRequestDto updateReviewRequestDto, String image) {
        this.starRate = updateReviewRequestDto.getStarRate();
        this.title = updateReviewRequestDto.getTitle();
        this.content = updateReviewRequestDto.getContent();
        this.reviewImage = image;

    }
}
