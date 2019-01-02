package com.bwee.springboot.gae.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author bradwee2000@gmail.com
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {

  public static final String INVALID_CREDENTIALS = "Invalid credentials.";
  public static final String MISSING_TOKEN = "You need to be logged in.";

  public static final AuthenticationException invalidCredentials() {
    return new AuthenticationException(INVALID_CREDENTIALS);
  }

  public static final AuthenticationException missingToken() {
    return new AuthenticationException(MISSING_TOKEN);
  }

  public AuthenticationException(final String msg) {
    super(msg);
  }
}
