package ua.university.sms.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.university.sms.exception.ConflictException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private final EnrollmentRequest request = new EnrollmentRequest(1L, 2L, 1, 2026);

    @Test
    void createFailsForUnknownStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.create(request));
    }

    @Test
    void createFailsForUnknownCourse() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student()));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.create(request));
    }

    @Test
    void createFailsForDuplicateEnrollment() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student()));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course()));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndStudyYear(1L, 2L, 1, 2026))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> enrollmentService.create(request));
    }

    @Test
    void createStartsWithNaGradeAndUnpaid() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student()));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course()));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndStudyYear(1L, 2L, 1, 2026))
                .thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> {
            Enrollment enrollment = invocation.getArgument(0);
            enrollment.setId(10L);
            return enrollment;
        });

        EnrollmentResponse response = enrollmentService.create(request);

        assertEquals(Grade.NA, response.grade());
        assertFalse(response.paid());
        assertEquals(1, response.semester());
        assertEquals(2026, response.year());
    }

    @Test
    void markPaidSetsPaidThroughPayable() {
        Enrollment enrollment = enrollment();
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment));

        EnrollmentResponse response = enrollmentService.markPaid(10L);

        assertTrue(response.paid());
        assertTrue(enrollment.isPaid());
    }

    @Test
    void setGradeUpdatesGrade() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(enrollment()));

        EnrollmentResponse response = enrollmentService.setGrade(10L, Grade.A);

        assertEquals(Grade.A, response.grade());
    }

    private Student student() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Ivan");
        student.setLastName("Petrenko");
        return student;
    }

    private Course course() {
        Course course = new Course();
        course.setId(2L);
        course.setName("Databases");
        course.setCredits(4);
        return course;
    }

    private Enrollment enrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(10L);
        enrollment.setStudent(student());
        enrollment.setCourse(course());
        enrollment.setSemester(1);
        enrollment.setStudyYear(2026);
        return enrollment;
    }
}
