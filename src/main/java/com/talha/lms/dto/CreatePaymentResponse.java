package com.talha.lms.dto;

public record CreatePaymentResponse(
        String paymentIntentId,
        String clientSecret
) {}
