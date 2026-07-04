package ua.university.sms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.StudentStatus;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    Page<Student> findByEnrollmentYear(Integer enrollmentYear, Pageable pageable);

    Page<Student> findByStatusAndEnrollmentYear(StudentStatus status, Integer enrollmentYear, Pageable pageable);

    boolean existsByEmail(String email);

    @Query("""
            select s from Student s
            where lower(concat(s.firstName, ' ', s.lastName)) like lower(concat('%', :query, '%'))
               or lower(s.email) like lower(concat('%', :query, '%'))
            """)
    Page<Student> searchByNameOrEmail(@Param("query") String query, Pageable pageable);

    @Query(value = "select distinct s from Student s join s.enrollments e where e.paid = false",
            countQuery = "select count(distinct s) from Student s join s.enrollments e where e.paid = false")
    Page<Student> findWithUnpaidEnrollments(Pageable pageable);
}
