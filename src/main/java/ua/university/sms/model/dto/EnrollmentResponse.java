package ua.university.sms.model.dto;

import ua.university.sms.model.enums.Grade;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        Integer semester,
        Integer year,
        Grade grade,
        boolean paid
) {
}
