package ua.university.sms.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.service.StudentService;
import ua.university.sms.service.TranscriptService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private TranscriptService transcriptService;

    @Test
    void createReturns201WithBody() throws Exception {
        when(studentService.create(any())).thenReturn(
                new StudentResponse(1L, "Test", "User", "test.user@univ.ua", 2025, StudentStatus.ACTIVE));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"User","email":"test.user@univ.ua",
                                 "enrollmentYear":2025,"status":"ACTIVE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test.user@univ.ua"));
    }

    @Test
    void createWithInvalidEmailReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"User","email":"not-an-email",
                                 "enrollmentYear":2025,"status":"ACTIVE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    void getUnknownStudentReturns404() throws Exception {
        when(studentService.get(99L)).thenThrow(new ResourceNotFoundException("Student with id 99 not found"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student with id 99 not found"));
    }
}
