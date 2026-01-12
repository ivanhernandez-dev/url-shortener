package dev.ivanhernandez.urlshortener.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exceptions")
class DomainExceptionsTest {

    @Test
    @DisplayName("UrlNotFoundException should contain short code in message")
    void urlNotFoundException_shouldContainShortCodeInMessage() {
        String shortCode = "abc123";

        UrlNotFoundException exception = new UrlNotFoundException(shortCode);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(shortCode));
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("ExpiredUrlException should contain short code in message")
    void expiredUrlException_shouldContainShortCodeInMessage() {
        String shortCode = "expired123";

        ExpiredUrlException exception = new ExpiredUrlException(shortCode);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(shortCode));
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("InvalidUrlException should contain custom message")
    void invalidUrlException_shouldContainCustomMessage() {
        String message = "Custom alias already exists";

        InvalidUrlException exception = new InvalidUrlException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("UrlOwnershipException should contain short code in message")
    void urlOwnershipException_shouldContainShortCodeInMessage() {
        String shortCode = "owned123";

        UrlOwnershipException exception = new UrlOwnershipException(shortCode);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(shortCode));
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("all domain exceptions should be unchecked exceptions")
    void allDomainExceptions_shouldBeUncheckedExceptions() {
        assertInstanceOf(RuntimeException.class, new UrlNotFoundException("test"));
        assertInstanceOf(RuntimeException.class, new ExpiredUrlException("test"));
        assertInstanceOf(RuntimeException.class, new InvalidUrlException("test"));
        assertInstanceOf(RuntimeException.class, new UrlOwnershipException("test"));
    }
}
