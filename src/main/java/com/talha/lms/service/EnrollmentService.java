package com.talha.lms.service;

import com.talha.lms.model.Course;
import com.talha.lms.model.Enrollment;
import com.talha.lms.model.Student;
import com.talha.lms.repo.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment enrollIfNotExists(Student student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseGet(() -> enrollmentRepository.save(Enrollment.builder()
                        .student(student)
                        .course(course)
                        .enrolledAt(OffsetDateTime.now())
                        .build()));
    }
}