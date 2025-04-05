package com.example.springrest.specification;

import com.example.springrest.entity.Student;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class StudentSpecification {

    public static Specification<Student> hasName(String name) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), name);
    }

    public static Specification<Student> hasEmail(String email) {
        return (root, query, cb) -> cb.like(root.get("email"), email + "%");
    }

    public static Specification<Student> nameLike(String pattern) {
        return (root, query, cb) -> cb.like(root.get("firstName"), pattern + "%");
    }

    public static Specification<Student> hasDobAfter(Date dobFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dob"), dobFrom);
    }

    public static Specification<Student> hasDobBefore(Date dobTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dob"), dobTo);
    }

    // Фильтрация по id курса
    public static Specification<Student> hasCourse(Long courseId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("enrollments").get("course").get("id"), courseId);
    }

}
