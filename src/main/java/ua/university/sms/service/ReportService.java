package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.StudentMapper;
import ua.university.sms.model.dto.CourseGpaResponse;
import ua.university.sms.model.dto.SemesterGpaResponse;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TopStudentResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ReportService(StudentRepository studentRepository, CourseRepository courseRepository,
                         EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> unpaidStudents(Pageable pageable) {
        return studentRepository.findWithUnpaidEnrollments(pageable).map(StudentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CourseGpaResponse courseGpa(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", courseId));
        Double average = enrollmentRepository.findAverageGpaByCourse(courseId);
        long graded = enrollmentRepository.countGradedByCourse(courseId);
        return new CourseGpaResponse(course.getId(), course.getName(), round(average), graded);
    }

    @Transactional(readOnly = true)
    public SemesterGpaResponse semesterGpa(Integer year, Integer semester) {
        Double average = enrollmentRepository.findAverageGpaBySemester(year, semester);
        long graded = enrollmentRepository.countGradedBySemester(year, semester);
        return new SemesterGpaResponse(year, semester, round(average), graded);
    }

    @Transactional(readOnly = true)
    public List<TopStudentResponse> topStudents(int limit) {
        return enrollmentRepository.findTopStudents(PageRequest.of(0, limit)).stream()
                .map(projection -> new TopStudentResponse(
                        projection.getStudentId(),
                        projection.getFullName(),
                        round(projection.getGpa())))
                .toList();
    }

    private BigDecimal round(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
