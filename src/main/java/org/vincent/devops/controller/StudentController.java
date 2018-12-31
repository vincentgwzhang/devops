package org.vincent.devops.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vincent.devops.dto.StudentDTO;
import org.vincent.devops.service.StudentService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
@RequestMapping("student")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudent() {
        return new ResponseEntity<>(studentService.getAllStudents(), OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {
        return new ResponseEntity<>(studentService.getStudent(id), OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<StudentDTO> getStudentByName(@PathVariable String name) {
        return new ResponseEntity<>(studentService.getStudent(name), OK);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> insertNewStudent(@RequestBody @NotNull StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.insertStudent(studentDTO), CREATED);
    }

    @PutMapping
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody @NotNull StudentDTO studentDTO) {
        studentService.updateStudent(studentDTO);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<StudentDTO> deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(OK);
    }

}
