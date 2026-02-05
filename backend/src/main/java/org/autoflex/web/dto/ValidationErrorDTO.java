package org.autoflex.web.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrorDTO extends WebErrorDTO {
    public List<FieldMessageDTO> errors = new ArrayList<>();

    public ValidationErrorDTO() {
    }

    public ValidationErrorDTO(Instant timestamp, int status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public void addError(String field, String message) {
        errors.add(new FieldMessageDTO(field, message));
    }
}
