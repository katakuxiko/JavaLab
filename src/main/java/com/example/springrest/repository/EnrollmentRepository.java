package com.example.springrest.repository;

import com.example.springrest.entity.Enrollment;
import com.example.springrest.entity.Student;
import com.example.springrest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByStudent(Student student);
}
