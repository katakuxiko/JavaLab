package com.example.springrest.service;

import com.example.springrest.entity.Student;
import com.example.springrest.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

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

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
