package isa.vezbe3.jpa_example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isa.vezbe3.jpa_example.model.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	
	@Query("select c from Course c join fetch c.exams e where c.id =?1")
	public Course findOneWithExams(Integer courseId);

}
