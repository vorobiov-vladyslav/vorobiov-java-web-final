package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.university.sms.exception.ErrorResponse;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.service.StudentService;
import ua.university.sms.service.TranscriptService;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student management, search and transcript")
public class StudentController {

    private final StudentService studentService;
    private final TranscriptService transcriptService;

    public StudentController(StudentService studentService, TranscriptService transcriptService) {
        this.studentService = studentService;
        this.transcriptService = transcriptService;
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    @ApiResponse(responseCode = "201", description = "Student created")
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Email already in use",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    @GetMapping
    @Operation(summary = "List students with pagination, optionally filtered by status and enrollment year")
    public Page<StudentResponse> list(@RequestParam(required = false) StudentStatus status,
                                      @RequestParam(required = false) Integer year,
                                      @ParameterObject @PageableDefault(sort = "id") Pageable pageable) {
        return studentService.list(status, year, pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by part of full name or email")
    public Page<StudentResponse> search(@RequestParam @NotBlank String query,
                                        @ParameterObject @PageableDefault(sort = "id") Pageable pageable) {
        return studentService.search(query, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by id")
    @ApiResponse(responseCode = "200", description = "Student found")
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public StudentResponse get(@PathVariable Long id) {
        return studentService.get(id);
    }

    @GetMapping("/{id}/transcript")
    @Operation(summary = "Get a student transcript with calculated GPA")
    @ApiResponse(responseCode = "200", description = "Transcript with GPA")
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TranscriptResponse transcript(@PathVariable Long id) {
        return transcriptService.getTranscript(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    @ApiResponse(responseCode = "200", description = "Student updated")
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student together with own enrollments")
    @ApiResponse(responseCode = "204", description = "Student deleted")
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
