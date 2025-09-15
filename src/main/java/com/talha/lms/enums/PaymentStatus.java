package com.talha.lms.enums;

public enum PaymentStatus {
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    PROCESSING,
    REQUIRES_ACTION,
    SUCCEEDED,
    CANCELED,
    UNKNOWN
}
