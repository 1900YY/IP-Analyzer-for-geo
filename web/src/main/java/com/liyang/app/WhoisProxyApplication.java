package com.liyang.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@SpringBootApplication
@RestController
@EnableCaching
public class WhoisProxyApplication {

    // 速率限制桶 (15分钟内最多100个请求)
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(15))))
                    .build();

    public static void main(String[] args) {
        SpringApplication.run(WhoisProxyApplication.class, args);
    }

    @GetMapping("/whois/{domain}")
    public ResponseEntity<?> getWhoisInfo(@PathVariable String domain) {
        // 检查速率限制
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many requests", "message", "Rate limit exceeded"));
        }

        System.out.println("Received WHOIS request for domain: " + domain);
        try {
            Map<String, Object> result = getCachedWhoisData(domain);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("WHOIS lookup failed for " + domain + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "WHOIS lookup failed", "details", e.getMessage()));
        }
    }

    @Cacheable(value = "whoisCache", key = "#domain")
    public Map<String, Object> getCachedWhoisData(String domain) throws Exception {
        String whoisData = queryWhoisServer(domain);
        System.out.println("WHOIS data received for " + domain);

        String creationDate = extractCreationDate(whoisData);
        String expirationDate = extractExpirationDate(whoisData);
        String registrar = extractRegistrar(whoisData);

        System.out.println("Extracted info for " + domain +
                ": Creation: " + creationDate +
                ", Expiration: " + expirationDate +
                ", Registrar: " + registrar);

        Map<String, Object> result = new HashMap<>();
        result.put("domain", domain);
        result.put("creationDate", creationDate);
        result.put("expirationDate", expirationDate);
        result.put("registrar", registrar);
        result.put("rawData", whoisData);

        return result;
    }

    private String queryWhoisServer(String domain) throws Exception {
        try (Socket socket = new Socket("whois.iana.org", 43)) {
            socket.getOutputStream().write((domain + "\r\n").getBytes());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            return response.toString();
        }
    }

    private String extractCreationDate(String whoisData) {
        Pattern pattern = Pattern.compile("(?:Creation|Registration) (?:Date|Time): (.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(whoisData);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    private String extractExpirationDate(String whoisData) {
        Pattern pattern = Pattern.compile("(?:Expiration) (?:Date|Time): (.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(whoisData);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    private String extractRegistrar(String whoisData) {
        Pattern pattern = Pattern.compile("Registrar: (.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(whoisData);
        if (!matcher.find()) {
            System.out.println("Could not extract registrar from WHOIS data");
            return "Unknown";
        }
        return matcher.group(1).trim();
    }
}
