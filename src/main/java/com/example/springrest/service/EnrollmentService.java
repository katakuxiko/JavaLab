package com.example.springrest.service;

import com.example.springrest.entity.Enrollment;
import com.example.springrest.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
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

    // ✅ Частичное обновление (PATCH)
    public Enrollment updateEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    // ✅ Удалить запись о зачислении
    public boolean deleteEnrollment(Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
