package kz.janapost.digitalhr.learning.repository;

import kz.janapost.digitalhr.learning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByIsActiveTrueOrderByTitleAsc();
}
