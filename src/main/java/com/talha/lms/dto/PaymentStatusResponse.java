package com.talha.lms.dto;

import com.talha.lms.enums.PaymentStatus;

public record PaymentStatusResponse(
        String paymentIntentId,
        PaymentStatus status
) {}