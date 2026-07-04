package ua.university.sms.model.dto;

import jakarta.validation.constraints.NotNull;
import ua.university.sms.model.enums.Grade;

public record GradeRequest(
        @NotNull Grade grade
) {
}
