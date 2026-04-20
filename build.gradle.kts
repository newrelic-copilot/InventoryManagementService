import org.gradle.wrapper.Download

plugins {
    id("java")
    id("de.undercouch.download") version "5.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register("downloadNewrelic") {
    doLast {
            val newrelicDir = file("newrelic")
            if (!newrelicDir.exists()) {
                newrelicDir.mkdirs() // Create the directory if it doesn't exist
            }
        ant.invokeMethod("get", mapOf(
            "src" to "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip",
            "dest" to file("newrelic/newrelic-java.zip")
        ))
    }
}

tasks.register<Copy>("unzipNewrelic") {
    from(zipTree(file("newrelic/newrelic-java.zip")))
    into(rootDir)
}

dependencies {
    implementation ("commons-fileupload:commons-fileupload:1.3.3")
    implementation ("org.apache.commons:commons-lang3:3.9")
    implementation ("org.apache.commons:commons-collections4:4.4")

    implementation ("org.springframework.boot:spring-boot-starter-web:2.5.10") // Secure and stable

    // Upgrade to Log4j2 which resolves vulnerabilities found in Log4j 1.x
    implementation ("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation ("org.apache.logging.log4j:log4j-api:2.14.1")

    // Upgrade to latest Gson version
    implementation ("com.google.code.gson:gson:2.8.9")


    implementation ("com.google.guava:guava:18.0")

    implementation ("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    implementation ("com.fasterxml.jackson.core:jackson-core:2.20.1")

    implementation ("com.fasterxml.jackson.core:jackson-annotations:2.20.1")

    implementation ("commons-net:commons-net:3.6")


    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")

}

tasks.test {
    useJUnitPlatform()
}