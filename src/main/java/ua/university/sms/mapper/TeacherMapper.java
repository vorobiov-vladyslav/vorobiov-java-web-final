package ua.university.sms.mapper;

import ua.university.sms.model.dto.TeacherRequest;
import ua.university.sms.model.dto.TeacherResponse;
import ua.university.sms.model.entity.Teacher;

public final class TeacherMapper {

    private TeacherMapper() {
    }

    public static Teacher toEntity(TeacherRequest request) {
        Teacher teacher = new Teacher();
        apply(teacher, request);
        return teacher;
    }

    public static void apply(Teacher teacher, TeacherRequest request) {
        teacher.setFirstName(request.firstName());
        teacher.setLastName(request.lastName());
        teacher.setEmail(request.email());
        teacher.setDateOfBirth(request.dateOfBirth());
        teacher.setPosition(request.position());
    }

    public static TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getEmail(),
                teacher.getDateOfBirth(),
                teacher.getPosition()
        );
    }
}
