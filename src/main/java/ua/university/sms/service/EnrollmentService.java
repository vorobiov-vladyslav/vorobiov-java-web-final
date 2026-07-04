package ua.university.sms.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ConflictException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.EnrollmentMapper;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;
import ua.university.sms.model.enums.Grade;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public EnrollmentResponse create(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ResourceNotFoundException.of("Student", request.studentId()));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> ResourceNotFoundException.of("Course", request.courseId()));
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndStudyYear(
                request.studentId(), request.courseId(), request.semester(), request.year())) {
            throw new ConflictException("Student " + request.studentId() + " is already enrolled in course "
                    + request.courseId() + " for semester " + request.semester() + " of " + request.year());
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(request.semester());
        enrollment.setStudyYear(request.year());
        enrollment.setGrade(Grade.NA);
        enrollment.setPaid(false);
        return EnrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse get(Long id) {
        return EnrollmentMapper.toResponse(getEntity(id));
    }

    @Transactional
    public EnrollmentResponse setGrade(Long id, Grade grade) {
        Enrollment enrollment = getEntity(id);
        enrollment.setGrade(grade);
        return EnrollmentMapper.toResponse(enrollment);
    }

    @Transactional
    public EnrollmentResponse markPaid(Long id) {
        Enrollment enrollment = getEntity(id);
        enrollment.markPaid();
        return EnrollmentMapper.toResponse(enrollment);
    }

    private Enrollment getEntity(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Enrollment", id));
    }
}
