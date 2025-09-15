package com.talha.lms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

import com.talha.lms.enums.PaymentStatus;
import com.talha.lms.model.Course;
import com.talha.lms.model.Student;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentIntentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Student student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Course course;

    private Long amountCents;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}