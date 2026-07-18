package com.example.repas_sur_backend.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new TestController())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    @Test
    void handleNotFound_returns404WithSafeJsonBody() throws Exception {
        mockMvc.perform(get("/test/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("missing resource"))
            .andExpect(jsonPath("$.path").value("/test/not-found"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void handleValidation_returns400WithGenericMessage() throws Exception {
        mockMvc.perform(
            post("/test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"value\":\"\"}")
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Validation error"))
            .andExpect(jsonPath("$.path").value("/test/validate"));
    }

    @Test
    void handleIllegalArgument_returns400WithOriginalMessage() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("bad data"))
            .andExpect(jsonPath("$.path").value("/test/illegal-argument"));
    }

    @Test
    void handleAccessDenied_returns403() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.message").value("Access denied"))
            .andExpect(jsonPath("$.path").value("/test/forbidden"));
    }

    @Test
    void handleGeneric_returns500WithoutSensitiveDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("Unexpected error"))
            .andExpect(jsonPath("$.path").value("/test/unexpected"))
            .andExpect(jsonPath("$.exception").doesNotExist());
    }

    @RestController
    @Validated
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        String notFound() {
            throw new NotFoundException("missing resource");
        }

        @GetMapping("/illegal-argument")
        String illegalArgument() {
            throw new IllegalArgumentException("bad data");
        }

        @GetMapping("/forbidden")
        String forbidden() {
            throw new AuthorizationDeniedException("forbidden");
        }

        @GetMapping("/unexpected")
        String unexpected() {
            throw new RuntimeException("secret internals");
        }

        @PostMapping("/validate")
        String validate(@Valid @RequestBody ValidationPayload payload) {
            return payload.value();
        }
    }

    record ValidationPayload(@NotBlank String value) {
    }
}
