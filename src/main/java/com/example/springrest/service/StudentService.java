package com.example.springrest.service;

import com.example.springrest.entity.Student;
import com.example.springrest.repository.StudentRepository;
import com.example.springrest.specification.StudentSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Student updateStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setFirstName(updatedStudent.getFirstName());
                    existingStudent.setLastName(updatedStudent.getLastName());
                    existingStudent.setEmail(updatedStudent.getEmail());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Student patchStudent(Long id, Student updatedFields) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    if (updatedFields.getFirstName() != null) {
                        existingStudent.setFirstName(updatedFields.getFirstName());
                    }
                    if (updatedFields.getLastName() != null) {
                        existingStudent.setLastName(updatedFields.getLastName());
                    }
                    if (updatedFields.getEmail() != null) {
                        existingStudent.setEmail(updatedFields.getEmail());
                    }
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Page<Student> getStudentsWithFilter(String name, String nameLike, String email, Date dobFrom, Date dobTo, Pageable pageable) {
        Specification<Student> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(StudentSpecification.hasName(name));
        }
        if (nameLike != null) {
            spec = spec.and(StudentSpecification.nameLike(nameLike));
        }
        if (email != null) {
            spec = spec.and(StudentSpecification.hasEmail(email));
        }
        if (dobFrom != null) {
            spec = spec.and(StudentSpecification.hasDobAfter(dobFrom));
        }
        if (dobTo != null) {
            spec = spec.and(StudentSpecification.hasDobBefore(dobTo));
        }

        return studentRepository.findAll(spec, pageable);
    }
}
