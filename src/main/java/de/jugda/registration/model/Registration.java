package de.jugda.registration.model;

import com.amazonaws.util.StringUtils;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@NoArgsConstructor
@RegisterForReflection
public class Registration {
    private String id;
    private String eventId;
    private String name;
    private String email;
    private boolean pub;
    private boolean waitlist;
    private boolean privacy;
    private LocalDateTime created;
    private Long ttl;

    public static Registration of(RegistrationForm form) {
        Registration registration = new Registration();
        registration.setEventId(form.getEventId());
        registration.setName(form.getName().trim());
        registration.setEmail(form.getEmail().trim().toLowerCase());
        registration.setPrivacy(onOrOff(form.getPrivacy()));
        registration.setPub(onOrOff(form.getPub()));
        registration.setWaitlist(form.isWaitlist());
        registration.setTtl(LocalDate.parse(form.getEventId()).plusWeeks(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC));
        return registration;
    }

    public static Registration from(Map<String, AttributeValue> item) {
        Registration registration = new Registration();
        if (item != null && !item.isEmpty()) {
            registration.setId(item.get("id").s());
            registration.setEventId(item.get("eventId").s());
            registration.setName(item.get("name").s());
            registration.setEmail(item.get("email").s());
            registration.setPub(item.get("pub").bool());
            registration.setWaitlist(item.get("waitlist").bool());
            registration.setPrivacy(item.get("privacy").bool());
            registration.setCreated(LocalDateTime.parse(item.get("created").s()));
            registration.setTtl(Long.valueOf(item.get("ttl").n()));
        }
        return registration;
    }

    public Map<String, AttributeValue> toItem() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id == null ? UUID.randomUUID().toString() : id).build());
        item.put("eventId", AttributeValue.builder().s(eventId).build());
        item.put("name", AttributeValue.builder().s(name).build());
        item.put("email", AttributeValue.builder().s(email).build());
        item.put("pub", AttributeValue.builder().bool(pub).build());
        item.put("waitlist", AttributeValue.builder().bool(waitlist).build());
        item.put("privacy", AttributeValue.builder().bool(privacy).build());
        item.put("created", AttributeValue.builder().s(
            (created == null ? LocalDateTime.now() : created).format(DateTimeFormatter.ISO_DATE_TIME)
        ).build());
        item.put("ttl", AttributeValue.builder().n(ttl.toString()).build());
        return item;
    }

    private static boolean onOrOff(String s) {
        return (StringUtils.isNullOrEmpty(s) ? "off" : s).equalsIgnoreCase("on");
    }
}
