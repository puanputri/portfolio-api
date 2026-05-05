package dev.puanputri.portfolio.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.stream.Collectors;

/**
 * Global exception mapper that converts exceptions to RFC 7807 problem+json responses.
 */
public class GlobalExceptionMapper {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    private static final MediaType PROBLEM_JSON =
            MediaType.valueOf("application/problem+json");

    /**
     * Handle bean-validation failures → 422 Unprocessable Entity.
     */
    @ServerExceptionMapper
    public Response handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(cv -> fieldName(cv) + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail problem = ProblemDetail.of(
                422,
                "Unprocessable Entity",
                detail,
                null
        );

        return Response.status(422)
                .type(PROBLEM_JSON)
                .entity(problem)
                .build();
    }

    /**
     * Pass-through for JAX-RS WebApplicationExceptions (e.g. 404, 429 thrown manually).
     */
    @ServerExceptionMapper
    public Response handleWebApplicationException(WebApplicationException ex) {
        int status = ex.getResponse().getStatus();
        String reason = ex.getMessage() != null ? ex.getMessage() : Response.Status.fromStatusCode(status).getReasonPhrase();

        ProblemDetail problem = ProblemDetail.of(
                status,
                Response.Status.fromStatusCode(status) != null
                        ? Response.Status.fromStatusCode(status).getReasonPhrase()
                        : "Error",
                reason,
                null
        );

        return Response.status(status)
                .type(PROBLEM_JSON)
                .entity(problem)
                .build();
    }

    /**
     * Catch-all → 500 Internal Server Error.
     */
    @ServerExceptionMapper
    public Response handleGenericException(Exception ex) {
        LOG.errorf(ex, "Unhandled exception: %s", ex.getMessage());

        ProblemDetail problem = ProblemDetail.of(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                null
        );

        return Response.status(500)
                .type(PROBLEM_JSON)
                .entity(problem)
                .build();
    }

    private String fieldName(ConstraintViolation<?> cv) {
        String path = cv.getPropertyPath().toString();
        // Strip method/param prefix (e.g. "submit.request.email" → "email")
        int lastDot = path.lastIndexOf('.');
        return lastDot >= 0 ? path.substring(lastDot + 1) : path;
    }
}
