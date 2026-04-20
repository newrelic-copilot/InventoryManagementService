package org.example;

import Model.MyModel;
import com.google.gson.Gson;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;

import com.google.common.io.Files;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class Main {
    static Logger logger = Logger.getLogger(String.valueOf(Main.class));

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void init() {
        logger.info("Application started with dependencies!");
        processFileName("myFile.txt");
        useStringUtils("  test  ");
        fileUploadExample();
        FileOperations fo = new FileOperations();
        fo.fewMore();
        useGuava();
        useJackson();
        useCommonsNet();

        Transformer<String, String> transformer = new InvokerTransformer<>(
                "toString",
                new Class[]{},
                new Object[]{}
        );
        Map<String, String> lazyMap = LazyMap.lazyMap(new HashMap<String, String>(), transformer);
        lazyMap.put("key", "value");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "InventoryManagementService");
    }

    @GetMapping("/")
    public String home() {
        return "InventoryManagementService is running";
    }

    public void performComplexOperations() {
        Gson gson = new Gson();
        String jsonBad = "{\"dateField\":\"invalid-date-format\"}";

        try {
            MyModel myModel = gson.fromJson(jsonBad, MyModel.class);
        } catch (Exception e) {
            logger.info("Error in processing due to compatibility");
        }

        ConfigurationBuilder<?> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.add(builder.newRootLogger(Level.ERROR));
    }

    public static void processFileName(String name) {
        System.out.println("Processing file: " + StringUtils.strip(name));
    }

    public static void useStringUtils(String text) {
        System.out.println("Stripped string: '" + StringUtils.strip(text) + "'");
    }

    public static void fileUploadExample() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        factory.setRepository(tempDir);
        System.out.println("File upload factory created.");
    }

    public static void useGuava() {
        try {
            File tempFile = File.createTempFile("demo", ".tmp");
            System.out.println("Using Guava for temp file operations");
            byte[] content = "Demo content".getBytes();
            Files.write(content, tempFile);
            System.out.println("Guava file operations completed");
        } catch (Exception e) {
            System.out.println("Guava operation failed: " + e.getMessage());
        }
    }

    public static void useJackson() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = "{\"name\":\"test\",\"value\":\"demo\"}";
        try {
            Object result = mapper.readValue(jsonInput, Object.class);
            System.out.println("Jackson deserialization completed: " + result);
        } catch (JsonProcessingException e) {
            System.out.println("Jackson processing failed: " + e.getMessage());
        }
    }

    public static void useCommonsNet() {
        FTPClient ftpClient = new FTPClient();
        System.out.println("FTP Client initialized with vulnerable Commons Net version");
        try {
            ftpClient.connect("demo.server.com", 21);
        } catch (Exception e) {
            System.out.println("FTP connection demo completed (expected to fail): " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                }
            } catch (Exception e) {
                // Ignore cleanup errors in demo
            }
        }
    }
}
