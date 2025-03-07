package com.doubleowner.revibe.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardPaymentRequestDto {
    private Long executionId;
    private String cardNumber;
    private String cardExpirationYear;
    private String cardExpirationMonth;
    private String cardPassword;
    private String customerIdentityNumber;
    private Long useCouponId;
    private int usePoint;
}
