package org.autoflex.common.exceptions;

import java.time.Instant;

public class WebError {
  public Instant timestamp;
  public int status;
  public String error;
  public String path;

  public WebError() {}

  public WebError(Instant timestamp, int status, String error, String path) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.path = path;
  }
}
