package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import ua.university.sms.exception.ErrorResponse;
import ua.university.sms.model.dto.TeacherRequest;
import ua.university.sms.model.dto.TeacherResponse;
import ua.university.sms.service.TeacherService;

@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teachers", description = "Teacher management")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @Operation(summary = "Create a new teacher")
    @ApiResponse(responseCode = "201", description = "Teacher created")
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Email already in use",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeacherResponse> create(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(request));
    }

    @GetMapping
    @Operation(summary = "List teachers with pagination")
    public Page<TeacherResponse> list(@ParameterObject @PageableDefault(sort = "id") Pageable pageable) {
        return teacherService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a teacher by id")
    @ApiResponse(responseCode = "200", description = "Teacher found")
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TeacherResponse get(@PathVariable Long id) {
        return teacherService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a teacher")
    @ApiResponse(responseCode = "200", description = "Teacher updated")
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return teacherService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a teacher without assigned courses")
    @ApiResponse(responseCode = "204", description = "Teacher deleted")
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Teacher is assigned to courses",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
