package ua.university.sms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.university.sms.model.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByEmail(String email);
}
