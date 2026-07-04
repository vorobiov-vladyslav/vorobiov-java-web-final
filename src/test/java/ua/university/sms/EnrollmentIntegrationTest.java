package ua.university.sms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.university.sms.model.dto.CourseRequest;
import ua.university.sms.model.dto.CourseResponse;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.dto.GradeRequest;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.model.enums.StudentStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class EnrollmentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void fullEnrollmentLifecycleProducesTranscriptWithGpa() {
        ResponseEntity<StudentResponse> studentCreated = restTemplate.postForEntity("/api/students",
                new StudentRequest("Integration", "Tester", "integration.tester@test.ua",
                        2026, StudentStatus.ACTIVE),
                StudentResponse.class);
        assertEquals(HttpStatus.CREATED, studentCreated.getStatusCode());
        Long studentId = studentCreated.getBody().id();

        ResponseEntity<CourseResponse> courseCreated = restTemplate.postForEntity("/api/courses",
                new CourseRequest("Integration Course", 4, "End-to-end scenario course", null),
                CourseResponse.class);
        assertEquals(HttpStatus.CREATED, courseCreated.getStatusCode());
        Long courseId = courseCreated.getBody().id();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(studentId, courseId, 1, 2030);
        ResponseEntity<EnrollmentResponse> enrollmentCreated = restTemplate.postForEntity("/api/enrollments",
                enrollmentRequest, EnrollmentResponse.class);
        assertEquals(HttpStatus.CREATED, enrollmentCreated.getStatusCode());
        EnrollmentResponse enrollment = enrollmentCreated.getBody();
        assertEquals(Grade.NA, enrollment.grade());
        assertFalse(enrollment.paid());

        ResponseEntity<String> duplicate = restTemplate.postForEntity("/api/enrollments",
                enrollmentRequest, String.class);
        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());

        ResponseEntity<EnrollmentResponse> graded = restTemplate.exchange(
                "/api/enrollments/" + enrollment.id() + "/grade", HttpMethod.PUT,
                new HttpEntity<>(new GradeRequest(Grade.A)), EnrollmentResponse.class);
        assertEquals(HttpStatus.OK, graded.getStatusCode());
        assertEquals(Grade.A, graded.getBody().grade());

        ResponseEntity<EnrollmentResponse> paid = restTemplate.exchange(
                "/api/enrollments/" + enrollment.id() + "/paid", HttpMethod.PUT,
                HttpEntity.EMPTY, EnrollmentResponse.class);
        assertEquals(HttpStatus.OK, paid.getStatusCode());
        assertTrue(paid.getBody().paid());

        ResponseEntity<TranscriptResponse> transcript = restTemplate.getForEntity(
                "/api/students/" + studentId + "/transcript", TranscriptResponse.class);
        assertEquals(HttpStatus.OK, transcript.getStatusCode());
        TranscriptResponse body = transcript.getBody();
        assertNotNull(body);
        assertEquals(1, body.items().size());
        assertEquals("Integration Course", body.items().getFirst().courseName());
        assertEquals(new BigDecimal("4.00"), body.gpa());
    }
}
