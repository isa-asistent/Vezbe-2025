package isa.vezbe3.jpa_example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isa.vezbe3.jpa_example.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

}
