package ua.university.sms.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.model.enums.StudentStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class StudentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void findByStatusAndEnrollmentYearReturnsOnlyMatchingStudents() {
        studentRepository.save(student("Marko", "Onleave", "marko.onleave@test.ua",
                2031, StudentStatus.ON_LEAVE));

        Page<Student> page = studentRepository.findByStatusAndEnrollmentYear(
                StudentStatus.ON_LEAVE, 2031, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("marko.onleave@test.ua", page.getContent().getFirst().getEmail());
    }

    @Test
    void searchMatchesPartOfFullNameOrEmail() {
        studentRepository.save(student("Zorian", "Testovych", "zorian.testovych@test.ua",
                2030, StudentStatus.ACTIVE));

        Page<Student> byFullName = studentRepository.searchByNameOrEmail(
                "zorian testo", PageRequest.of(0, 10));
        Page<Student> byEmail = studentRepository.searchByNameOrEmail(
                "testovych@", PageRequest.of(0, 10));

        assertTrue(byFullName.getContent().stream()
                .anyMatch(found -> found.getEmail().equals("zorian.testovych@test.ua")));
        assertTrue(byEmail.getContent().stream()
                .anyMatch(found -> found.getEmail().equals("zorian.testovych@test.ua")));
    }

    @Test
    void unpaidReportContainsOnlyStudentsWithUnpaidEnrollments() {
        Course course = new Course();
        course.setName("Repository Test Course");
        course.setCredits(4);
        courseRepository.save(course);

        Student unpaidStudent = studentRepository.save(student("Unpaid", "Debtor",
                "unpaid.debtor@test.ua", 2030, StudentStatus.ACTIVE));
        Student paidStudent = studentRepository.save(student("Paid", "Solvent",
                "paid.solvent@test.ua", 2030, StudentStatus.ACTIVE));
        enrollmentRepository.save(enrollment(unpaidStudent, course, false));
        enrollmentRepository.save(enrollment(paidStudent, course, true));

        Page<Student> page = studentRepository.findWithUnpaidEnrollments(PageRequest.of(0, 100));

        assertTrue(page.getContent().stream()
                .anyMatch(found -> found.getEmail().equals("unpaid.debtor@test.ua")));
        assertFalse(page.getContent().stream()
                .anyMatch(found -> found.getEmail().equals("paid.solvent@test.ua")));
    }

    private Student student(String firstName, String lastName, String email, int year, StudentStatus status) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setEnrollmentYear(year);
        student.setStatus(status);
        return student;
    }

    private Enrollment enrollment(Student student, Course course, boolean paid) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(1);
        enrollment.setStudyYear(2032);
        enrollment.setGrade(Grade.NA);
        enrollment.setPaid(paid);
        return enrollment;
    }
}
