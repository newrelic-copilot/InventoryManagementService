package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.core.SpringVersion;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringFrameworkVersionTest {

    @Test
    void usesPatchedSpringFrameworkVersion() {
        String version = SpringVersion.getVersion();

        assertNotNull(version);
        assertTrue(isPatchedVersion(version), "Expected Spring Framework 5.3.18+ but got " + version);
    }

    private boolean isPatchedVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            return false;
        }

        int major = parseLeadingNumber(parts[0]);
        int minor = parseLeadingNumber(parts[1]);
        int patch = parseLeadingNumber(parts[2]);
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Unexpected Spring Framework version: " + version);
        }

        return major > 5 || (major == 5 && minor > 3) || (major == 5 && minor == 3 && patch >= 18);
    }

    private int parseLeadingNumber(String versionPart) {
        String numericPrefix = versionPart.split("[^0-9]", 2)[0];
        return numericPrefix.isEmpty() ? -1 : Integer.parseInt(numericPrefix);
    }
}
