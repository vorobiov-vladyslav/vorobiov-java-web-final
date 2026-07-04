package ua.university.sms.model.dto;

import java.math.BigDecimal;

public record SemesterGpaResponse(
        Integer year,
        Integer semester,
        BigDecimal averageGpa,
        long gradedEnrollments
) {
}
