package ua.university.sms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI universityOpenApi() {
        return new OpenAPI().info(new Info()
                .title("University Management System API")
                .description("RESTful API for managing students, teachers, courses, enrollments, "
                        + "payments, grades, transcripts and GPA reports")
                .version("1.0.0"));
    }
}
