package org.autoflex.web.dto;

import java.time.Instant;

public class WebErrorDTO {
    public Instant timestamp;
    public int status;
    public String error;
    public String path;

    public WebErrorDTO() {
    }

    public WebErrorDTO(Instant timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }
}
