package org.vincent.devops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vincent.devops.dozer.DozerMapper;
import org.vincent.devops.dto.StudentDTO;
import org.vincent.devops.entity.Student;
import org.vincent.devops.repository.StudentRepository;
import org.vincent.devops.system.handling.exceptions.StudentDuplicateException;
import org.vincent.devops.system.handling.exceptions.StudentNotFoundException;

import java.util.List;
import java.util.Optional;


@Service
public class StudentService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final StudentRepository studentRepository;

    private final DozerMapper mapper;

    @Autowired
    public StudentService(StudentRepository studentRepository, DozerMapper mapper) {
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public List<StudentDTO> getAllStudents() {
        logger.info("function {} called", "getAllStudents");
        return mapper.map(studentRepository.findAll(), StudentDTO.class);
    }

    @Transactional
    public StudentDTO getStudent(int id) {
        logger.info("function {} called, parameters: {}", "getStudent", id);
        assertStudentExist(id);
        Student student =  studentRepository.findById(id).orElseThrow(() -> new RuntimeException());
        return mapper.map(student, StudentDTO.class);
    }

    @Transactional
    public StudentDTO getStudent(String name) {
        logger.info("function {} called, parameters: {}", "getStudent", name);
        assertStudentExist(name);
        List<Student> students = studentRepository.findByName(name);
        return mapper.map(students.get(0), StudentDTO.class);
    }

    @Transactional
    public StudentDTO insertStudent(StudentDTO studentDTO) {
        logger.info("function {} called, parameters: {}", "insertStudent", studentDTO);
        assertNameNotDuplicate(studentDTO.getName());
        Student student = mapper.map(studentDTO, Student.class);
        student = studentRepository.save(student);
        return mapper.map(student, StudentDTO.class);
    }

    @Transactional
    public StudentDTO updateStudent(StudentDTO studentDTO) {
        logger.info("function {} called, parameters: {}", "updateStudent", studentDTO);
        assertStudentExist(studentDTO.getId());
        Student student = mapper.map(studentDTO, Student.class);
        student = studentRepository.save(student);
        return mapper.map(student, StudentDTO.class);
    }

    @Transactional
    public void deleteStudent(int id) {
        logger.info("function {} called, parameters: {}", "deleteStudent", id);
        assertStudentExist(id);
        studentRepository.deleteById(id);
    }

    private void assertStudentExist(int id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        if(!studentOptional.isPresent()) {
            throw new StudentNotFoundException(id);
        }
    }

    private void assertStudentExist(String name) {
        List<Student> students = studentRepository.findByName(name);
        if(students.isEmpty()) {
            throw new StudentNotFoundException(name);
        }
    }

    private void assertNameNotDuplicate(String name) {
        List<Student> students = studentRepository.findByName(name);
        if(!students.isEmpty()) {
            throw new StudentDuplicateException(name);
        }
    }


}
