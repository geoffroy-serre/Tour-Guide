package com.tourGuide.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception if a User is not found to deal with a null result.
 * Isn't throw automatically. Have to be intentionally thrown.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNameNotFound extends RuntimeException {
  public UserNameNotFound() {
    super();
  }

}
