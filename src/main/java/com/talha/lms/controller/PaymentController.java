package com.talha.lms.controller;

import com.talha.lms.dto.CreatePaymentRequest;
import com.talha.lms.dto.CreatePaymentResponse;
import com.talha.lms.dto.PaymentStatusResponse;
import com.talha.lms.model.Payment;
import com.talha.lms.repo.PaymentRepository;
import com.talha.lms.service.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @PostMapping("/create-intent")
    public CreatePaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) throws StripeException {
        return paymentService.createPaymentIntent(request);
    }

    @GetMapping("/{paymentIntentId}")
    public PaymentStatusResponse getStatus(@PathVariable String paymentIntentId) {
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return new PaymentStatusResponse(paymentIntentId, payment.getStatus());
    }

    @GetMapping("/config/publishable-key")
    public String publishableKey() {
        return publishableKey;
    }
}