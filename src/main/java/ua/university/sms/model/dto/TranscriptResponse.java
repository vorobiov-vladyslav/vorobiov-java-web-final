package ua.university.sms.model.dto;

import ua.university.sms.model.enums.StudentStatus;

import java.math.BigDecimal;
import java.util.List;

public record TranscriptResponse(
        Long studentId,
        String fullName,
        String email,
        Integer enrollmentYear,
        StudentStatus status,
        List<TranscriptItem> items,
        BigDecimal gpa
) {
}
