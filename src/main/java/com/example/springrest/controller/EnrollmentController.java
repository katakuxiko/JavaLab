package com.example.springrest.controller;

import com.example.springrest.dto.EnrollmentRequest;
import com.example.springrest.entity.Course;
import com.example.springrest.entity.Enrollment;
import com.example.springrest.entity.Student;
import com.example.springrest.service.CourseService;
import com.example.springrest.service.EnrollmentService;
import com.example.springrest.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService, StudentService studentService, CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    // ✅ Получить все зачисления
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<Page<Enrollment>> getAllEnrollments(Pageable pageable) {
        Page<Enrollment> enrollments = enrollmentService.getAllEnrollments(pageable);
        return ResponseEntity.ok(enrollments);
    }

    // Получить зачисление по ID
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Long id) {
        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Создать зачисление (POST)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public ResponseEntity<Enrollment> createEnrollment(@RequestBody EnrollmentRequest request) {
            Optional<Student> student = studentService.getStudentById(request.getStudentId());
            Optional<Course> course = courseService.getCourseById(request.getCourseId());

            if (student.isPresent() && course.isPresent()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student.get());
                enrollment.setCourse(course.get());
                enrollment.setEnrollmentDate(request.getEnrollmentDate());
                enrollment.setStatus(request.getStatus());

                Enrollment savedEnrollment = enrollmentService.createEnrollment(enrollment);
                return ResponseEntity.ok(savedEnrollment);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> updateEnrollment(
            @PathVariable Long id,
            @RequestBody EnrollmentRequest request) {

        Optional<Enrollment> existingEnrollment = enrollmentService.getEnrollmentById(id);
        Optional<Student> student = studentService.getStudentById(request.getStudentId());
        Optional<Course> course = courseService.getCourseById(request.getCourseId());

        if (existingEnrollment.isPresent() && student.isPresent() && course.isPresent()) {
            Enrollment enrollment = existingEnrollment.get();
            enrollment.setStudent(student.get());
            enrollment.setCourse(course.get());
            enrollment.setEnrollmentDate(request.getEnrollmentDate());
            enrollment.setStatus(request.getStatus());

            Enrollment updatedEnrollment = enrollmentService.createEnrollment(enrollment);
            return ResponseEntity.ok(updatedEnrollment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }





    // ✅ Частичное обновление (PATCH)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PatchMapping("/{id}")
    public ResponseEntity<Enrollment> partialUpdateEnrollment(
            @PathVariable Long id,
            @RequestBody EnrollmentRequest request) {

        return enrollmentService.getEnrollmentById(id)
                .map(enrollment -> {
                    if (request.getStatus() != null) {
                        enrollment.setStatus(request.getStatus());
                    }
                    if (request.getEnrollmentDate() != null) {
                        enrollment.setEnrollmentDate(request.getEnrollmentDate());
                    }
                    if (request.getStudentId() != null) {
                        studentService.getStudentById(request.getStudentId()).ifPresent(enrollment::setStudent);
                    }
                    if (request.getCourseId() != null) {
                        courseService.getCourseById(request.getCourseId()).ifPresent(enrollment::setCourse);
                    }

                    Enrollment saved = enrollmentService.createEnrollment(enrollment);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // ✅ Удалить зачисление
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        if (enrollmentService.deleteEnrollment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Enrollment> getMyEnrollments() {
        return enrollmentService.getEnrollmentsForCurrentUser();
    }
}
