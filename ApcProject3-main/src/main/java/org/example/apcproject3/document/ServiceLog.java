package org.example.apcproject3.document;

// Temporarily disabled - MongoDB dependency removed from pom.xml
/*
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "service_logs")
public class ServiceLog {

    @Id
    private String id;

    @Field("booking_id")
    private Long bookingId;

    @Field("provider_id")
    private Long providerId;

    @Field("customer_id")
    private Long customerId;

    @Field("action")
    private String action; // BOOKING_CREATED, SERVICE_STARTED, SERVICE_COMPLETED, etc.

    @Field("description")
    private String description;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("ip_address")
    private String ipAddress;

    @Field("user_agent")
    private String userAgent;

    @Field("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors
    public ServiceLog() {}

    public ServiceLog(Long bookingId, Long providerId, Long customerId, String action, String description) {
        this.bookingId = bookingId;
        this.providerId = providerId;
        this.customerId = customerId;
        this.action = action;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
*/
