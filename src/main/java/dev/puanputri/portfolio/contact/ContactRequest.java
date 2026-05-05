package dev.puanputri.portfolio.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Inbound DTO for contact form submissions.
 * Validated by Jakarta Bean Validation before reaching the service layer.
 */
public class ContactRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must be at most 100 characters")
    public String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 254, message = "Email must be at most 254 characters")
    public String email;

    @NotBlank(message = "Message must not be blank")
    @Size(min = 10, max = 5000, message = "Message must be between 10 and 5000 characters")
    public String message;

    public ContactRequest() {
    }

    public ContactRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }
}
