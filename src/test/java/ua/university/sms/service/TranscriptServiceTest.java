package ua.university.sms.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private TranscriptService transcriptService;

    @Test
    void gpaIsWeightedByCourseCredits() {
        List<Enrollment> enrollments = List.of(enrollment(Grade.A, 5), enrollment(Grade.C, 3));

        assertEquals(new BigDecimal("3.25"), transcriptService.calculateGpa(enrollments));
    }

    @Test
    void gpaExcludesNotGradedEnrollments() {
        List<Enrollment> enrollments = List.of(enrollment(Grade.A, 5), enrollment(Grade.NA, 3));

        assertEquals(new BigDecimal("4.00"), transcriptService.calculateGpa(enrollments));
    }

    @Test
    void gpaIsNullWhenNothingIsGraded() {
        assertNull(transcriptService.calculateGpa(List.of()));
        assertNull(transcriptService.calculateGpa(List.of(enrollment(Grade.NA, 4))));
    }

    @Test
    void gpaIsRoundedToTwoDigitsHalfUp() {
        List<Enrollment> enrollments = List.of(
                enrollment(Grade.A, 1),
                enrollment(Grade.A, 1),
                enrollment(Grade.B, 1));

        assertEquals(new BigDecimal("3.67"), transcriptService.calculateGpa(enrollments));
    }

    @Test
    void transcriptContainsStudentInfoItemsAndGpa() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Ivan");
        student.setLastName("Petrenko");
        student.setEmail("ivan@univ.ua");
        student.setEnrollmentYear(2024);
        student.setStatus(StudentStatus.ACTIVE);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(enrollmentRepository.findAllByStudentIdWithCourse(1L))
                .thenReturn(List.of(enrollment(Grade.B, 4), enrollment(Grade.NA, 2)));

        TranscriptResponse transcript = transcriptService.getTranscript(1L);

        assertEquals(1L, transcript.studentId());
        assertEquals("Ivan Petrenko", transcript.fullName());
        assertEquals(2, transcript.items().size());
        assertEquals(new BigDecimal("3.00"), transcript.gpa());
    }

    @Test
    void transcriptOfUnknownStudentThrowsNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transcriptService.getTranscript(99L));
    }

    private Enrollment enrollment(Grade grade, int credits) {
        Course course = new Course();
        course.setName("Course " + credits);
        course.setCredits(credits);
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setSemester(1);
        enrollment.setStudyYear(2026);
        enrollment.setGrade(grade);
        return enrollment;
    }
}
