package com.doubleowner.revibe.domain.coupon.service;

import com.doubleowner.revibe.domain.coupon.dto.response.IssuedCouponResponseDto;
import com.doubleowner.revibe.domain.coupon.entity.Coupon;
import com.doubleowner.revibe.domain.coupon.entity.CouponStatus;
import com.doubleowner.revibe.domain.coupon.entity.IssuedCoupon;
import com.doubleowner.revibe.domain.coupon.repository.CouponRepository;
import com.doubleowner.revibe.domain.coupon.repository.IssuedCouponRepository;
import com.doubleowner.revibe.domain.user.entity.User;
import com.doubleowner.revibe.global.aop.DistributedLock;
import com.doubleowner.revibe.global.exception.CustomException;
import com.doubleowner.revibe.global.exception.errorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.doubleowner.revibe.global.exception.errorCode.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @DistributedLock(key = "#couponId")
    public IssuedCouponResponseDto issuedCoupon(Long id, User user) {

        // 쿠폰 조회
        Coupon findCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_VALUE));

        if (issuedCouponRepository.existsByCouponIdAndUserId(findCoupon.getId(),user.getId())) {
            throw new CustomException(ALREADY_ISSUED_COUPON);
        }

        // 쿠폰 발급 저장
        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .coupon(findCoupon)
                .user(user)
                .status(CouponStatus.ACTIVE)
                .build();

        IssuedCoupon savedCoupon = issuedCouponRepository.save(issuedCoupon);
        extracted(findCoupon);

        return IssuedCoupon.toDto(savedCoupon);
    }

    @Transactional
    public void extracted(Coupon findCoupon) {
        findCoupon.decrementCoupon();
        System.out.println("현재 쿠폰 개수 : " + findCoupon.getTotalQuantity());
        couponRepository.save(findCoupon);
    }

    // 사용자에게 발급된 모든 쿠폰 조회
    public List<IssuedCouponResponseDto> getUserCoupons(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUser(user, pageable);

        return issuedCoupons.stream()
                .map(IssuedCouponResponseDto::new)
                .collect(Collectors.toList());
    }

    // 쿠폰 사용 현황
    public void usedCoupon(Long id, User user) {

        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_VALUE));

        checkCouponValidity(user, issuedCoupon);

        issuedCoupon.usedCoupon();
        issuedCouponRepository.save(issuedCoupon);

    }

    // 쿠폰 유효성 검증
//    @Transactional
    public void checkCouponValidity(User user, IssuedCoupon issuedCoupon) {
        if (!issuedCoupon.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.INVALID_COUPON_CODE);
        }

        if (issuedCoupon.getStatus().equals(CouponStatus.USED)) {
            throw new CustomException(ErrorCode.ALREADY_USED_COUPON);
        }
    }
}