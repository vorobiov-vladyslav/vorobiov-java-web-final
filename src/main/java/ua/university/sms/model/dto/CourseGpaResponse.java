package ua.university.sms.model.dto;

import java.math.BigDecimal;

public record CourseGpaResponse(
        Long courseId,
        String courseName,
        BigDecimal averageGpa,
        long gradedEnrollments
) {
}
