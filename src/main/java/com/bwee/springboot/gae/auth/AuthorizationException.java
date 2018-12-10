package com.bwee.springboot.gae.auth;

import com.google.common.base.Joiner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

/**
 * @author bradwee2000@gmail.com
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AuthorizationException extends RuntimeException {

  public AuthorizationException(Exception e) {
    super(e);
  }

  public AuthorizationException(final VerifiedUser user, final Collection<String> requiredRoles) {
    super("User " + user.getId() + " has insufficient roles: "
        + Joiner.on(", ").join(requiredRoles));
  }
}
