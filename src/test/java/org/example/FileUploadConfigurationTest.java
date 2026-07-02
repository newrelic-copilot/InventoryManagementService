package org.example;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUploadConfigurationTest {

    @Test
    void mainConfiguresSecureMultipartLimits() {
        ServletFileUpload upload = Main.createSecureFileUpload();

        assertEquals(10L * 1024 * 1024, upload.getFileSizeMax());
        assertEquals(20L * 1024 * 1024, upload.getSizeMax());
        assertEquals(20, upload.getFileCountMax());
        assertEquals(8 * 1024, upload.getPartHeaderSizeMax());
    }

    @Test
    void fileOperationsConfiguresSecureMultipartLimits() {
        ServletFileUpload upload = FileOperations.createSecureFileUpload();

        assertEquals(10L * 1024 * 1024, upload.getFileSizeMax());
        assertEquals(20L * 1024 * 1024, upload.getSizeMax());
        assertEquals(20, upload.getFileCountMax());
        assertEquals(8 * 1024, upload.getPartHeaderSizeMax());
    }
}
