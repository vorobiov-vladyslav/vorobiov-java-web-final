package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.university.sms.exception.ErrorResponse;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.dto.GradeRequest;
import ua.university.sms.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Enrollment, grading and payment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @Operation(summary = "Enroll a student in a course; grade starts as NA and payment as unpaid")
    @ApiResponse(responseCode = "201", description = "Enrollment created")
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Student or course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Student is already enrolled in the course for this semester",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an enrollment by id")
    @ApiResponse(responseCode = "200", description = "Enrollment found")
    @ApiResponse(responseCode = "404", description = "Enrollment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public EnrollmentResponse get(@PathVariable Long id) {
        return enrollmentService.get(id);
    }

    @PutMapping("/{id}/grade")
    @Operation(summary = "Set a grade for an enrollment")
    @ApiResponse(responseCode = "200", description = "Grade set")
    @ApiResponse(responseCode = "400", description = "Invalid grade value",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Enrollment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public EnrollmentResponse setGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        return enrollmentService.setGrade(id, request.grade());
    }

    @PutMapping("/{id}/paid")
    @Operation(summary = "Mark an enrollment as paid")
    @ApiResponse(responseCode = "200", description = "Enrollment marked as paid")
    @ApiResponse(responseCode = "404", description = "Enrollment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public EnrollmentResponse markPaid(@PathVariable Long id) {
        return enrollmentService.markPaid(id);
    }
}
