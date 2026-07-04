package ua.university.sms.model.dto;

import ua.university.sms.model.enums.TeacherPosition;

import java.time.LocalDate;

public record TeacherResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        TeacherPosition position
) {
}
