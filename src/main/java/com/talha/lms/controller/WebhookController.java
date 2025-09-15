package com.talha.lms.controller;

import com.talha.lms.model.Course;
import com.talha.lms.model.Payment;
import com.talha.lms.model.Student;
import com.talha.lms.repo.CourseRepository;
import com.talha.lms.repo.PaymentRepository;
import com.talha.lms.repo.StudentRepository;
import com.talha.lms.service.EnrollmentService;
import com.talha.lms.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/webhook/stripe")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;

    @Value("${STRIPE_WEBHOOK_SECRET:}")
    private String webhookSecret;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.error("Webhook secret not configured");
            throw new IllegalStateException("Webhook secret not configured");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("⚠️  Webhook signature verification failed.");
            throw new IllegalArgumentException("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            default -> log.debug("Unhandled event type: {}", event.getType());
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);
        log.info("PaymentIntent succeeded: {}", paymentIntent.getId());
        paymentService.syncPaymentFromStripeEvent(paymentIntent);

        // Enroll student
        String studentId = paymentIntent.getMetadata().get("studentId");
        String courseId = paymentIntent.getMetadata().get("courseId");
        if (studentId != null && courseId != null) {
            Optional<Payment> pOpt = paymentRepository.findByPaymentIntentId(paymentIntent.getId());
            if (pOpt.isPresent()) {
                Student student = studentRepository.findById(Long.valueOf(studentId)).orElse(null);
                Course course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);
                if (student != null && course != null) {
                    enrollmentService.enrollIfNotExists(student, course);
                    log.info("Enrollment created for student {} in course {}", student.getId(), course.getId());
                }
            }
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = extractPaymentIntent(event);
        log.info("PaymentIntent failed: {}", paymentIntent.getId());
        paymentService.syncPaymentFromStripeEvent(paymentIntent);
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException("Unable to deserialize PaymentIntent"));
    }
}