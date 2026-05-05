package dev.puanputri.portfolio.event;

import dev.puanputri.portfolio.common.ProblemDetail;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST resource for logging analytics events (page views, project clicks).
 */
@Path("/api/v1/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Events", description = "Analytics event tracking endpoints")
public class EventResource {

    @Inject
    EventService eventService;

    @POST
    @Blocking
    @Operation(summary = "Log an analytics event",
               description = "Records a page view or project click event for analytics purposes.")
    @APIResponse(responseCode = "204", description = "Event logged successfully")
    @APIResponse(responseCode = "422", description = "Validation failed",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public Response log(@Valid EventRequest request) {
        eventService.log(request);
        return Response.noContent().build();
    }
}
