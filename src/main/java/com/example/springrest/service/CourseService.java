package com.example.springrest.service;

import com.example.springrest.entity.Course;
import com.example.springrest.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Course updateCourse(Long id, Course updatedCourse) {
        return courseRepository.findById(id).map(course -> {
            course.setName(updatedCourse.getName());
            course.setCredits(updatedCourse.getCredits());
            return courseRepository.save(course);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Course patchCourse(Long id, Course updatedFields) {
        return courseRepository.findById(id).map(course -> {
            Integer newCredits = updatedFields.getCredits();
            if (updatedFields.getName() != null) course.setName(updatedFields.getName());
            if (newCredits != null) {
                course.setCredits(newCredits);
            } else {
                course.setCredits(0);  // ✅ Default value instead of null
            }            return courseRepository.save(course);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public void deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Course not found");
        }
    }
}
