package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ConflictException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.CourseMapper;
import ua.university.sms.model.dto.CourseRequest;
import ua.university.sms.model.dto.CourseResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Teacher;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.TeacherRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, TeacherRepository teacherRepository,
                         EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        Course course = new Course();
        applyRequest(course, request);
        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseResponse get(Long id) {
        return CourseMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> list(Long teacherId, Integer credits, Pageable pageable) {
        Page<Course> page;
        if (teacherId != null && credits != null) {
            page = courseRepository.findByTeacherIdAndCredits(teacherId, credits, pageable);
        } else if (teacherId != null) {
            page = courseRepository.findByTeacherId(teacherId, pageable);
        } else if (credits != null) {
            page = courseRepository.findByCredits(credits, pageable);
        } else {
            page = courseRepository.findAll(pageable);
        }
        return page.map(CourseMapper::toResponse);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = getEntity(id);
        applyRequest(course, request);
        return CourseMapper.toResponse(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = getEntity(id);
        if (enrollmentRepository.existsByCourseId(id)) {
            throw new ConflictException("Course with id " + id + " has enrollments and cannot be deleted");
        }
        courseRepository.delete(course);
    }

    private void applyRequest(Course course, CourseRequest request) {
        course.setName(request.name());
        course.setCredits(request.credits());
        course.setDescription(request.description());
        course.setTeacher(resolveTeacher(request.teacherId()));
    }

    private Teacher resolveTeacher(Long teacherId) {
        if (teacherId == null) {
            return null;
        }
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> ResourceNotFoundException.of("Teacher", teacherId));
    }

    private Course getEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", id));
    }
}
