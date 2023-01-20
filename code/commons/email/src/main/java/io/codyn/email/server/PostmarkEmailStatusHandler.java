package io.codyn.email.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PostmarkEmailStatusHandler {

    static final String RECORD_TYPE = "RecordType";
    static final String BOUNCE = "Bounce";
    private static final Logger log = LoggerFactory.getLogger(PostmarkEmailStatusHandler.class);

    public void handle(Map<String, Object> statusData) {
        var recordType = statusData.get(RECORD_TYPE);
        if (recordType == null) {
            log.warn("Invalid email status data, no {}: {}", RECORD_TYPE, statusData);
        } else if (recordType.equals(BOUNCE)) {
            log.error("We got email bounce: {}", statusData);
        } else {
            log.info("We got email status: {}", statusData);
        }
    }
}
