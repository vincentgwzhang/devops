package org.vincent.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vincent.devops.entity.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findByName(String name);

}
