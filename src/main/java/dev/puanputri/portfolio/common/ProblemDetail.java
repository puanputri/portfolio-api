package dev.puanputri.portfolio.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;

/**
 * RFC 7807 Problem Detail representation.
 * Serialised as {@code application/problem+json}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        URI type,
        String title,
        int status,
        String detail,
        String instance
) {

    /** Convenience factory for common cases. */
    public static ProblemDetail of(int status, String title, String detail, String instance) {
        return new ProblemDetail(
                URI.create("about:blank"),
                title,
                status,
                detail,
                instance
        );
    }
}
