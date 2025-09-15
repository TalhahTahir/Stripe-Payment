package com.talha.lms.service;

import com.talha.lms.dto.CreatePaymentRequest;
import com.talha.lms.dto.CreatePaymentResponse;
import com.talha.lms.enums.PaymentStatus;
import com.talha.lms.model.*;
import com.talha.lms.repo.CourseRepository;
import com.talha.lms.repo.PaymentRepository;
import com.talha.lms.repo.StudentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.currency:usd}")
    private String currency;

    @Transactional
    public CreatePaymentResponse createPaymentIntent(CreatePaymentRequest request) throws StripeException {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.isActive()) {
            throw new IllegalStateException("Course not active");
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        long amount = course.getPriceCents();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .setDescription("Enrollment for course: " + course.getTitle())
                .putMetadata("studentId", student.getId().toString())
                .putMetadata("courseId", course.getId().toString())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment payment = Payment.builder()
                .paymentIntentId(paymentIntent.getId())
                .student(student)
                .course(course)
                .amountCents(amount)
                .currency(currency)
                .status(mapStripeStatus(paymentIntent.getStatus()))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        paymentRepository.save(payment);

        return new CreatePaymentResponse(paymentIntent.getId(), paymentIntent.getClientSecret());
    }

    @Transactional
    public void syncPaymentFromStripeEvent(PaymentIntent paymentIntent) {
        paymentRepository.findByPaymentIntentId(paymentIntent.getId())
                .ifPresent(p -> {
                    p.setStatus(mapStripeStatus(paymentIntent.getStatus()));
                    p.setUpdatedAt(OffsetDateTime.now());
                });
    }

    private PaymentStatus mapStripeStatus(String stripeStatus) {
        if (stripeStatus == null) return PaymentStatus.UNKNOWN;
        return switch (stripeStatus) {
            case "requires_payment_method" -> PaymentStatus.REQUIRES_PAYMENT_METHOD;
            case "requires_confirmation" -> PaymentStatus.REQUIRES_CONFIRMATION;
            case "processing" -> PaymentStatus.PROCESSING;
            case "requires_action" -> PaymentStatus.REQUIRES_ACTION;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELED;
            default -> PaymentStatus.UNKNOWN;
        };
    }
}