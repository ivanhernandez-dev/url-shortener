package dev.ivanhernandez.urlshortener.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Url Domain Model - Parameterized Tests")
class UrlParameterizedTest {

    @ParameterizedTest
    @DisplayName("isExpired should return true for past dates")
    @ValueSource(ints = {1, 7, 30, 365})
    void isExpired_shouldReturnTrue_forPastDates(int daysInPast) {
        Url url = new Url();
        url.setExpiresAt(LocalDateTime.now().minusDays(daysInPast));

        assertTrue(url.isExpired());
    }

    @ParameterizedTest
    @DisplayName("isExpired should return false for future dates")
    @ValueSource(ints = {1, 7, 30, 365})
    void isExpired_shouldReturnFalse_forFutureDates(int daysInFuture) {
        Url url = new Url();
        url.setExpiresAt(LocalDateTime.now().plusDays(daysInFuture));

        assertFalse(url.isExpired());
    }

    @ParameterizedTest
    @DisplayName("isExpired should return false for null expiresAt")
    @NullSource
    void isExpired_shouldReturnFalse_forNullExpiresAt(LocalDateTime expiresAt) {
        Url url = new Url();
        url.setExpiresAt(expiresAt);

        assertFalse(url.isExpired());
    }

    @ParameterizedTest
    @DisplayName("incrementAccessCount should correctly increment from various starting values")
    @CsvSource({
            "0, 1",
            "1, 2",
            "10, 11",
            "100, 101",
            "999, 1000"
    })
    void incrementAccessCount_shouldIncrementCorrectly(Long initial, Long expected) {
        Url url = new Url();
        url.setAccessCount(initial);

        url.incrementAccessCount();

        assertEquals(expected, url.getAccessCount());
    }

    @ParameterizedTest
    @DisplayName("setters and getters should work correctly for various URLs")
    @ValueSource(strings = {
            "https://example.com",
            "https://www.google.com/search?q=test",
            "http://localhost:8080/api/v1/resource",
            "https://subdomain.domain.co.uk/path/to/resource?param=value#anchor"
    })
    void settersAndGetters_shouldWorkCorrectly_forVariousUrls(String originalUrl) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);

        assertEquals(originalUrl, url.getOriginalUrl());
    }
}
