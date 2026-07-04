package ua.university.sms.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ua.university.sms.model.enums.StudentStatus;

public record StudentRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotNull @Min(2000) @Max(2100) Integer enrollmentYear,
        @NotNull StudentStatus status
) {
}
