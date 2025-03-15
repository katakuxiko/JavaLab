package com.example.springrest.service;

import com.example.springrest.entity.Enrollment;
import com.example.springrest.entity.Student;
import com.example.springrest.entity.User;
import com.example.springrest.repository.EnrollmentRepository;
import com.example.springrest.repository.StudentRepository;
import com.example.springrest.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {


    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;


    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            UserService userService, UserService userService1,
            StudentRepository studentRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.userService = userService1;
        this.studentRepository = studentRepository;
    }

    // ✅ Получить все записи о зачислениях
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // ✅ Получить запись о зачислении по ID
    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    // ✅ Создать новую запись о зачислении
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
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
