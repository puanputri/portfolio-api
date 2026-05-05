package dev.puanputri.portfolio.contact;

import java.time.LocalDateTime;

/**
 * Outbound DTO returned after a successful contact form submission.
 */
public record ContactResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
