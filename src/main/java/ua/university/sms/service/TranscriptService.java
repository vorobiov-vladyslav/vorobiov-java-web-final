package ua.university.sms.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.EnrollmentMapper;
import ua.university.sms.model.dto.TranscriptItem;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TranscriptService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public TranscriptService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public TranscriptResponse getTranscript(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdWithCourse(studentId);
        List<TranscriptItem> items = enrollments.stream()
                .map(EnrollmentMapper::toTranscriptItem)
                .toList();
        return new TranscriptResponse(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getEnrollmentYear(),
                student.getStatus(),
                items,
                calculateGpa(enrollments)
        );
    }

    public BigDecimal calculateGpa(List<Enrollment> enrollments) {
        List<Enrollment> graded = enrollments.stream()
                .filter(enrollment -> enrollment.getGrade().isGraded())
                .toList();
        if (graded.isEmpty()) {
            return null;
        }
        double weightedPoints = 0.0;
        double totalCredits = 0.0;
        for (Enrollment enrollment : graded) {
            int credits = enrollment.getCourse().getCredits();
            weightedPoints += enrollment.getGrade().points() * credits;
            totalCredits += credits;
        }
        return BigDecimal.valueOf(weightedPoints / totalCredits).setScale(2, RoundingMode.HALF_UP);
    }
}
