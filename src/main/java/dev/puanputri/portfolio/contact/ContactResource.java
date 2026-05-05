package dev.puanputri.portfolio.contact;

import dev.puanputri.portfolio.common.ProblemDetail;
import dev.puanputri.portfolio.common.RateLimiterService;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.server.ServerRequestContext;

import java.net.URI;

/**
 * REST resource for handling contact form submissions.
 */
@Path("/api/v1/contact")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Contact", description = "Contact form endpoints")
public class ContactResource {

    @Inject
    ContactService contactService;

    @Inject
    RateLimiterService rateLimiterService;

    @POST
    @Blocking
    @Operation(summary = "Submit a contact form", description = "Validates input, persists to DB, and sends an email notification. Rate-limited to 5 requests per minute per IP.")
    @APIResponse(responseCode = "201", description = "Contact submitted successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ContactResponse.class)))
    @APIResponse(responseCode = "422", description = "Validation failed",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @APIResponse(responseCode = "429", description = "Rate limit exceeded",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public Response submit(
            @Valid ContactRequest request,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo
    ) {
        String clientIp = resolveClientIp(headers);

        if (!rateLimiterService.isAllowed(clientIp, 5, 60)) {
            ProblemDetail problem = ProblemDetail.of(
                    429,
                    "Too Many Requests",
                    "Rate limit exceeded: maximum 5 contact submissions per minute per IP address.",
                    uriInfo.getRequestUri().getPath()
            );
            return Response.status(429)
                    .type("application/problem+json")
                    .entity(problem)
                    .build();
        }

        ContactResponse response = contactService.submit(request, clientIp);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(response.id()))
                .build();

        return Response.created(location)
                .entity(response)
                .build();
    }

    /**
     * Resolve the real client IP, honoring X-Forwarded-For from reverse proxies.
     */
    private String resolveClientIp(HttpHeaders headers) {
        String forwardedFor = headers.getHeaderString("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For may be a comma-separated list; take the first (original client)
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = headers.getHeaderString("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        // Fallback — may be null in some test environments
        return "unknown";
    }
}
