package dev.puanputri.portfolio.contact;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Service layer for contact form submissions.
 * Persists the entity and dispatches an email notification.
 */
@ApplicationScoped
public class ContactService {

    private static final Logger LOG = Logger.getLogger(ContactService.class);

    @Inject
    ContactMapper contactMapper;

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "portfolio.contact.notification-email")
    String notificationEmail;

    /**
     * Persist a contact submission and send an email notification.
     *
     * @param request the validated contact form data
     * @param ip      the originating client IP address
     * @return a response DTO containing the persisted entity id and timestamps
     */
    @Transactional
    public ContactResponse submit(ContactRequest request, String ip) {
        Contact contact = contactMapper.toEntity(request);
        contact.ipAddress = ip;
        contact.persist();

        LOG.infof("Contact form submitted: id=%d, email=%s, ip=%s",
                contact.id, contact.email, ip);

        sendNotificationEmail(contact);

        return contactMapper.toResponse(contact);
    }

    private void sendNotificationEmail(Contact contact) {
        try {
            String subject = "New contact form submission from " + contact.name;
            String body = String.format(
                    "You have received a new message via your portfolio contact form.%n%n" +
                    "Name:    %s%n" +
                    "Email:   %s%n" +
                    "IP:      %s%n" +
                    "Message:%n%s%n",
                    contact.name,
                    contact.email,
                    contact.ipAddress,
                    contact.message
            );

            mailer.send(
                    Mail.withText(notificationEmail, subject, body)
                            .setReplyTo(contact.email)
            );

            LOG.infof("Notification email sent for contact id=%d", contact.id);
        } catch (Exception e) {
            // Log but do not re-throw — email failure must not roll back the DB transaction
            LOG.errorf(e, "Failed to send notification email for contact id=%d: %s",
                    contact.id, e.getMessage());
        }
    }
}
