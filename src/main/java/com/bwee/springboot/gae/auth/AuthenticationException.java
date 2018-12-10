package com.bwee.springboot.gae.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author bradwee2000@gmail.com
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {

  public AuthenticationException() {
    super("Invalid credentials.");
  }
}
