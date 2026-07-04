package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ConflictException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.TeacherMapper;
import ua.university.sms.model.dto.TeacherRequest;
import ua.university.sms.model.dto.TeacherResponse;
import ua.university.sms.model.entity.Teacher;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public TeacherService(TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        if (teacherRepository.existsByEmail(request.email())) {
            throw new ConflictException("Teacher with email " + request.email() + " already exists");
        }
        Teacher saved = teacherRepository.save(TeacherMapper.toEntity(request));
        return TeacherMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TeacherResponse get(Long id) {
        return TeacherMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> list(Pageable pageable) {
        return teacherRepository.findAll(pageable).map(TeacherMapper::toResponse);
    }

    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = getEntity(id);
        if (!teacher.getEmail().equals(request.email()) && teacherRepository.existsByEmail(request.email())) {
            throw new ConflictException("Teacher with email " + request.email() + " already exists");
        }
        TeacherMapper.apply(teacher, request);
        return TeacherMapper.toResponse(teacher);
    }

    @Transactional
    public void delete(Long id) {
        Teacher teacher = getEntity(id);
        if (courseRepository.existsByTeacherId(id)) {
            throw new ConflictException("Teacher with id " + id + " is assigned to courses and cannot be deleted");
        }
        teacherRepository.delete(teacher);
    }

    private Teacher getEntity(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
    }
}
