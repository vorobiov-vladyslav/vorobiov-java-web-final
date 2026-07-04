package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ConflictException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.mapper.StudentMapper;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new ConflictException("Student with email " + request.email() + " already exists");
        }
        Student saved = studentRepository.save(StudentMapper.toEntity(request));
        return StudentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentResponse get(Long id) {
        return StudentMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> list(StudentStatus status, Integer year, Pageable pageable) {
        Page<Student> page;
        if (status != null && year != null) {
            page = studentRepository.findByStatusAndEnrollmentYear(status, year, pageable);
        } else if (status != null) {
            page = studentRepository.findByStatus(status, pageable);
        } else if (year != null) {
            page = studentRepository.findByEnrollmentYear(year, pageable);
        } else {
            page = studentRepository.findAll(pageable);
        }
        return page.map(StudentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> search(String query, Pageable pageable) {
        return studentRepository.searchByNameOrEmail(query, pageable).map(StudentMapper::toResponse);
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getEntity(id);
        if (!student.getEmail().equals(request.email()) && studentRepository.existsByEmail(request.email())) {
            throw new ConflictException("Student with email " + request.email() + " already exists");
        }
        StudentMapper.apply(student, request);
        return StudentMapper.toResponse(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = getEntity(id);
        studentRepository.delete(student);
    }

    private Student getEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Student", id));
    }
}
