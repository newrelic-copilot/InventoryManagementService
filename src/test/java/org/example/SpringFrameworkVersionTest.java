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

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        return major > 5 || (major == 5 && minor > 3) || (major == 5 && minor == 3 && patch >= 18);
    }
}
