package ua.university.sms.model.dto;

import ua.university.sms.model.enums.Grade;

public record TranscriptItem(
        String courseName,
        Integer credits,
        Integer semester,
        Integer year,
        Grade grade,
        boolean paid
) {
}
