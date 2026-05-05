package dev.puanputri.portfolio.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Service layer for analytics event logging.
 */
@ApplicationScoped
public class EventService {

    private static final Logger LOG = Logger.getLogger(EventService.class);

    /**
     * Persist an analytics event to the database.
     *
     * @param request the inbound event data
     */
    @Transactional
    public void log(EventRequest request) {
        PageEvent event = new PageEvent();
        event.eventType = PageEvent.EventType.valueOf(request.eventType);
        event.page = request.page;
        event.referrer = request.referrer;
        event.userAgent = request.userAgent;
        event.persist();

        LOG.debugf("Analytics event logged: type=%s, page=%s, id=%d",
                event.eventType, event.page, event.id);
    }
}
