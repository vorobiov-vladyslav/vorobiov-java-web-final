package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.university.sms.exception.ErrorResponse;
import ua.university.sms.model.dto.CourseGpaResponse;
import ua.university.sms.model.dto.SemesterGpaResponse;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TopStudentResponse;
import ua.university.sms.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Aggregated reports built with Spring Data JPA queries")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/unpaid-students")
    @Operation(summary = "List students having at least one unpaid enrollment")
    public Page<StudentResponse> unpaidStudents(
            @ParameterObject @PageableDefault(sort = "id") Pageable pageable) {
        return reportService.unpaidStudents(pageable);
    }

    @GetMapping("/course-gpa/{courseId}")
    @Operation(summary = "Average GPA across graded enrollments of a course")
    @ApiResponse(responseCode = "200", description = "Average GPA calculated")
    @ApiResponse(responseCode = "404", description = "Course not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public CourseGpaResponse courseGpa(@PathVariable Long courseId) {
        return reportService.courseGpa(courseId);
    }

    @GetMapping("/semester-gpa")
    @Operation(summary = "Average GPA across graded enrollments of a semester")
    public SemesterGpaResponse semesterGpa(@RequestParam @Min(2000) @Max(2100) Integer year,
                                           @RequestParam @Min(1) @Max(2) Integer semester) {
        return reportService.semesterGpa(year, semester);
    }

    @GetMapping("/top-students")
    @Operation(summary = "Top-N students by credit-weighted GPA")
    public List<TopStudentResponse> topStudents(
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        return reportService.topStudents(limit);
    }
}
