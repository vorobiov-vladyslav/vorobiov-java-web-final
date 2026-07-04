package ua.university.sms.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull @Min(1) @Max(60) Integer credits,
        @Size(max = 2000) String description,
        Long teacherId
) {
}
