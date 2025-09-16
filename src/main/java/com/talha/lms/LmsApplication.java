package com.talha.lms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.talha.lms.model.Course;
import com.talha.lms.model.Student;
import com.talha.lms.repo.CourseRepository;
import com.talha.lms.repo.StudentRepository;

@SpringBootApplication
public class LmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}

	    @Bean
    CommandLineRunner seedData(CourseRepository courseRepository,
                               StudentRepository studentRepository) {
        return args -> {
            if (courseRepository.count() == 0) {
                courseRepository.save(Course.builder().title("Spring Boot Mastery")
                        .priceCents(19900L).active(true).build());
                courseRepository.save(Course.builder().title("Advanced Java")
                        .priceCents(14900L).active(true).build());
            }
            if (studentRepository.count() == 0) {
                studentRepository.save(Student.builder()
                        .email("mr.talhahtahir@gmail.com").fullName("Talha Tahir").build());
            }
        };
    }
}
