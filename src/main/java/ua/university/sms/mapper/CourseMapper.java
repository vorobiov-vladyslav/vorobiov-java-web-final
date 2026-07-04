package ua.university.sms.mapper;

import ua.university.sms.model.dto.CourseResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Teacher;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static CourseResponse toResponse(Course course) {
        Teacher teacher = course.getTeacher();
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getCredits(),
                course.getDescription(),
                teacher == null ? null : teacher.getId(),
                teacher == null ? null : teacher.getFirstName() + " " + teacher.getLastName()
        );
    }
}
