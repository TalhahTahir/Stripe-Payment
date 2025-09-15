package com.talha.lms.repo;

import com.talha.lms.model.Course;
import com.talha.lms.model.Enrollment;
import com.talha.lms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
}