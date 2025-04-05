package com.example.springrest.controller;

import com.example.springrest.entity.Student;
import com.example.springrest.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentsController {

    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
        public ResponseEntity<Page<Student>> getAllStudents(Pageable pageable) {
            Page<Student> students = studentService.getAllStudents(pageable);
            return ResponseEntity.ok(students);
    }

    @GetMapping("/filters")
    public Page<Student> filterStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "name_like") String nameLike,
            @RequestParam(required = false) String email,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
            @Schema(type = "string", example = "15.03.2024")
            Date dobFrom,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
            @Schema(type = "string", example = "15.03.2024")
            Date dobTo,
            Pageable pageable
    ) {
        return studentService.getStudentsWithFilter(name, nameLike, email,dobFrom, dobTo, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public Optional<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        return studentService.updateStudent(id, updatedStudent);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PatchMapping("/{id}")
    public Student patchStudent(@PathVariable Long id, @RequestBody Student updatedFields) {
        return studentService.patchStudent(id, updatedFields);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}

