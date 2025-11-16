package isa.vezbe3.jpa_example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isa.vezbe3.jpa_example.model.Exam;
import isa.vezbe3.jpa_example.repository.ExamRepository;

@Service
public class ExamService {
	
	@Autowired
	private ExamRepository examRepository;
	
	public Exam findOne(Integer id) {
		return examRepository.findById(id).orElseGet(null);
	}

	public List<Exam> findAll() {
		return examRepository.findAll();
	}
	
	public Exam save(Exam exam) {
		return examRepository.save(exam);
	}

	public void remove(Integer id) {
		examRepository.deleteById(id);
	}
}
