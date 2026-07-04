package ua.university.sms.mapper;

import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequest request) {
        Student student = new Student();
        apply(student, request);
        return student;
    }

    public static void apply(Student student, StudentRequest request) {
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setEnrollmentYear(request.enrollmentYear());
        student.setStatus(request.status());
    }

    public static StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getEnrollmentYear(),
                student.getStatus()
        );
    }
}
