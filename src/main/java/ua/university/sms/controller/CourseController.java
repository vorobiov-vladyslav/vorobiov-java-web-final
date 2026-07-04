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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.university.sms.exception.ErrorResponse;
import ua.university.sms.model.dto.CourseRequest;
import ua.university.sms.model.dto.CourseResponse;
import ua.university.sms.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course management and filtering")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @Operation(summary = "Create a new course, optionally assigned to a teacher")
    @ApiResponse(responseCode = "201", description = "Course created")
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Referenced teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    @GetMapping
    @Operation(summary = "List courses with pagination, optionally filtered by teacher and credits")
    public Page<CourseResponse> list(@RequestParam(required = false) Long teacherId,
                                     @RequestParam(required = false) Integer credits,
                                     @ParameterObject @PageableDefault(sort = "id") Pageable pageable) {
        return courseService.list(teacherId, credits, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by id")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public CourseResponse get(@PathVariable Long id) {
        return courseService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    @ApiResponse(responseCode = "200", description = "Course updated")
    @ApiResponse(responseCode = "404", description = "Course or referenced teacher not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course without enrollments")
    @ApiResponse(responseCode = "204", description = "Course deleted")
    @ApiResponse(responseCode = "404", description = "Course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Course has enrollments",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
