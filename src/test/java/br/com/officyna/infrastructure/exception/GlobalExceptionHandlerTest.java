package br.com.officyna.infrastructure.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        TestController testController = new TestController();
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders
                .standaloneSetup(testController)
                .setControllerAdvice(globalExceptionHandler)
                .setValidator(new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    class HandleNotFound {

        @Test
        void shouldReturnNotFoundWithCorrectStatus() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", equalTo(404)));
        }

        @Test
        void shouldReturnNotFoundWithCorrectMessage() throws Exception {
            String expectedMessage = "Resource not found";
            mockMvc.perform(get("/test/not-found")
                    .param("message", expectedMessage))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("not found")));
        }

        @Test
        void shouldReturnNotFoundWithNullErrors() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors", nullValue()));
        }

        @Test
        void shouldReturnNotFoundWithTimestamp() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }
    }

    @Nested
    class HandleDomainException {

        @Test
        void shouldReturnBadRequestWithCorrectStatus() throws Exception {
            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", equalTo(400)));
        }

        @Test
        void shouldReturnBadRequestWithCorrectMessage() throws Exception {
            mockMvc.perform(get("/test/domain-error")
                    .param("message", "Invalid domain state"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Invalid")));
        }

        @Test
        void shouldReturnBadRequestWithNullErrors() throws Exception {
            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", nullValue()));
        }

        @Test
        void shouldReturnBadRequestWithTimestamp() throws Exception {
            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }
    }

    @Nested
    class HandleValidationException {

        @Test
        void shouldReturnBadRequestWithCorrectStatus() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", equalTo(400)));
        }

        @Test
        void shouldReturnValidationFailedMessage() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", equalTo("Validation failed")));
        }

        @Test
        void shouldReturnValidationErrorsMap() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", notNullValue()))
                    .andExpect(jsonPath("$.errors", hasKey("name")));
        }

        @Test
        void shouldIncludeFieldErrorMessages() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name", notNullValue()));
        }

        @Test
        void shouldReturnTimestampForValidationError() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }

        @Test
        void shouldNotReturnErrorsAsNullForValidationException() throws Exception {
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", not(nullValue())));
        }

        @Test
        void shouldHandleMultipleFieldErrors() throws Exception {
            String invalidJson = "{ \"name\": \"\", \"age\": \"invalid\" }";
            mockMvc.perform(post("/test/validation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", equalTo("Validation failed")))
                    .andExpect(jsonPath("$.errors", notNullValue()));
        }
    }

    @Nested
    class HandleGenericException {

        @Test
        void shouldReturnInternalServerErrorWithCorrectStatus() throws Exception {
            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status", equalTo(500)));
        }

        @Test
        void shouldReturnGenericErrorMessage() throws Exception {
            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message",
                            equalTo("Internal server error. Please try again later.")));
        }

        @Test
        void shouldNotExposeSensitiveException() throws Exception {
            MvcResult result = mockMvc.perform(get("/test/generic-error"))
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            assertFalse(content.contains("NullPointerException"),
                    "Response should not contain exception class name");
        }

        @Test
        void shouldReturnTimestampForGenericError() throws Exception {
            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }

        @Test
        void shouldReturnNullErrorsForGenericException() throws Exception {
            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.errors", nullValue()));
        }
    }

    @Nested
    class ResponseStructure {

        @Test
        void shouldAlwaysIncludeStatus() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(jsonPath("$.status", notNullValue()));

            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(jsonPath("$.status", notNullValue()));

            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(jsonPath("$.status", notNullValue()));
        }

        @Test
        void shouldAlwaysIncludeMessage() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(jsonPath("$.message", notNullValue()));

            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(jsonPath("$.message", notNullValue()));

            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(jsonPath("$.message", notNullValue()));
        }

        @Test
        void shouldAlwaysIncludeTimestamp() throws Exception {
            mockMvc.perform(get("/test/not-found"))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));

            mockMvc.perform(get("/test/domain-error"))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));

            mockMvc.perform(get("/test/generic-error"))
                    .andExpect(jsonPath("$.timestamp", notNullValue()));
        }
    }

    /**
     * Test controller to simulate exception scenarios
     */
    @RestController
    public static class TestController {

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new NotFoundException("Resource not found");
        }

        @GetMapping("/test/domain-error")
        public void throwDomainException() {
            throw new DomainException("Invalid domain state");
        }

        @PostMapping("/test/validation")
        public void handleValidation(@RequestBody @jakarta.validation.Valid TestValidationRequest request) {
            // Request validation is handled by Spring automatically
        }

        @GetMapping("/test/generic-error")
        public void throwGenericException() {
            throw new RuntimeException("Unexpected error");
        }
    }

    /**
     * Test request class with validation annotations
     */
    static class TestValidationRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        private String email;

        public TestValidationRequest() {}

        public TestValidationRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}







