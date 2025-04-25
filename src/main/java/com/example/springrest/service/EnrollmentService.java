package com.example.springrest.service;

import com.example.springrest.entity.Enrollment;
import com.example.springrest.entity.Student;
import com.example.springrest.entity.User;
import com.example.springrest.repository.EnrollmentRepository;
import com.example.springrest.repository.StudentRepository;
import com.example.springrest.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {


    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            UserService userService, UserService userService1,
            StudentRepository studentRepository, EmailService emailService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.userService = userService1;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

    // ✅ Получить все записи о зачислениях
    public Page<Enrollment> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAll(pageable);
    }

    // ✅ Получить запись о зачислении по ID
    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    // ✅ Создать новую запись о зачислении
    public Enrollment createEnrollment(Enrollment enrollment) {
        Enrollment saved = enrollmentRepository.save(enrollment);

        Student student = saved.getStudent();
        if (student != null && student.getEmail() != null) {
            String studentName = student.getFirstName() + " " + student.getLastName();
            String courseTitle = saved.getCourse().getName(); // предполагается, что у Course есть getTitle()
            LocalDate enrollmentDate = saved.getEnrollmentDate();

            emailService.sendEnrollmentNotification(student.getEmail(), studentName, courseTitle, enrollmentDate);
        }

        return saved;
    }

    // ✅ Обновить запись о зачислении (PUT)
    public Optional<Enrollment> updateEnrollment(Long id, Enrollment updatedEnrollment) {
        return enrollmentRepository.findById(id).map(existingEnrollment -> {
            existingEnrollment.setStudent(updatedEnrollment.getStudent());
            existingEnrollment.setCourse(updatedEnrollment.getCourse());
            existingEnrollment.setStatus(updatedEnrollment.getStatus());
            existingEnrollment.setEnrollmentDate(updatedEnrollment.getEnrollmentDate());
            return enrollmentRepository.save(existingEnrollment);
        });
    }

    // ✅ Удалить запись о зачислении
    public boolean deleteEnrollment(Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Enrollment> getEnrollmentsForCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Student> studentOptional = studentRepository.findByUser(user);

                if (studentOptional.isPresent()) {
                    return enrollmentRepository.findByStudentId(studentOptional.get().getId());
                } else {
                    throw new RuntimeException("Пользователь не является студентом");
                }
            }
        }

        throw new RuntimeException("Не удалось определить текущего пользователя");
    }


}
