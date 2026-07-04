package ua.university.sms.model.dto;

import java.math.BigDecimal;

public record TopStudentResponse(
        Long studentId,
        String fullName,
        BigDecimal gpa
) {
}
