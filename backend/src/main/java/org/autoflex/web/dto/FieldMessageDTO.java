package org.autoflex.web.dto;

public class FieldMessageDTO {
    public String field;
    public String message;

    public FieldMessageDTO() {
    }

    public FieldMessageDTO(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
