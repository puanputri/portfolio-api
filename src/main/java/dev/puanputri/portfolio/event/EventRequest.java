package dev.puanputri.portfolio.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Inbound DTO for analytics event logging.
 */
public class EventRequest {

    @NotBlank(message = "eventType must not be blank")
    @Pattern(regexp = "PAGE_VIEW|PROJECT_CLICK",
             message = "eventType must be one of: PAGE_VIEW, PROJECT_CLICK")
    public String eventType;

    @NotBlank(message = "page must not be blank")
    @Size(max = 512, message = "page must be at most 512 characters")
    public String page;

    @Size(max = 1024, message = "referrer must be at most 1024 characters")
    public String referrer;

    @Size(max = 512, message = "userAgent must be at most 512 characters")
    public String userAgent;

    public EventRequest() {
    }

    public EventRequest(String eventType, String page, String referrer, String userAgent) {
        this.eventType = eventType;
        this.page = page;
        this.referrer = referrer;
        this.userAgent = userAgent;
    }
}
