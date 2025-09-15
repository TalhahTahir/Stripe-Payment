package com.talha.lms.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull Long courseId,
        @NotNull Long studentId
) {}