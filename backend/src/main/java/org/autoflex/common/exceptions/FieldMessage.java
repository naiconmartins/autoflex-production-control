package org.autoflex.common.exceptions;

public class FieldMessage {
  public String field;
  public String message;

  public FieldMessage() {}

  public FieldMessage(String field, String message) {
    this.field = field;
    this.message = message;
  }
}
