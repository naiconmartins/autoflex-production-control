package org.autoflex.common.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends WebError {
  public List<FieldMessage> errors = new ArrayList<>();

  public ValidationError() {}

  public ValidationError(Instant timestamp, int status, String error, String path) {
    super(timestamp, status, error, path);
  }

  public void addError(String field, String message) {
    errors.add(new FieldMessage(field, message));
  }
}
