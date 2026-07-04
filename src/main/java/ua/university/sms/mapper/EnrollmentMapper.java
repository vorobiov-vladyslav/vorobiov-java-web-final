package ua.university.sms.mapper;

import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.dto.TranscriptItem;
import ua.university.sms.model.entity.Enrollment;

public final class EnrollmentMapper {

    private EnrollmentMapper() {
    }

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFullName(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getSemester(),
                enrollment.getStudyYear(),
                enrollment.getGrade(),
                enrollment.isPaid()
        );
    }

    public static TranscriptItem toTranscriptItem(Enrollment enrollment) {
        return new TranscriptItem(
                enrollment.getCourse().getName(),
                enrollment.getCourse().getCredits(),
                enrollment.getSemester(),
                enrollment.getStudyYear(),
                enrollment.getGrade(),
                enrollment.isPaid()
        );
    }
}
