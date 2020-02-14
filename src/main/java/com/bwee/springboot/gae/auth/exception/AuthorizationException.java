package com.bwee.springboot.gae.auth.exception;

import com.bwee.springboot.gae.auth.user.AuthUser;
import com.google.common.base.Joiner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;
import java.util.Objects;

import static com.bwee.springboot.gae.auth.exception.AuthorizationException.ErrorType.EXPIRED_TOKEN;
import static com.bwee.springboot.gae.auth.exception.AuthorizationException.ErrorType.INSUFFICIENT_PERMISSIONS;
import static com.bwee.springboot.gae.auth.exception.AuthorizationException.ErrorType.INSUFFICIENT_ROLES;
import static com.bwee.springboot.gae.auth.exception.AuthorizationException.ErrorType.INVALID_TOKEN;
import static com.bwee.springboot.gae.auth.exception.AuthorizationException.ErrorType.MISSING_TOKEN;

/**
 * @author bradwee2000@gmail.com
 */
@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AuthorizationException extends RuntimeException {

  public static AuthorizationException missingToken() {
    return new AuthorizationException(MISSING_TOKEN);
  }

  public static AuthorizationException invalidToken(final String token) {
    return new AuthorizationException(INVALID_TOKEN, "Token: " + token);
  }

  public static AuthorizationException expiredToken(final String token) {
    return new AuthorizationException(EXPIRED_TOKEN, "Token: " + token);
  }

  public static AuthorizationException missingRoles(final AuthUser authUser, final Collection<String> roles) {
    final String msg = "User " + authUser + " does not have required roles: " + Joiner.on(',').join(roles);
    return new AuthorizationException(INSUFFICIENT_ROLES, msg);
  }

  public static AuthorizationException missingPermissions(final AuthUser authUser,
                                                          final Collection<String> permissions) {
    final String msg = "User " + authUser + " does not have required permissions: " + Joiner.on(',').join(permissions);
    return new AuthorizationException(INSUFFICIENT_PERMISSIONS, msg);
  }

  public enum ErrorType {
    INVALID_TOKEN("Invalid access token."),
    INSUFFICIENT_ROLES("You have insufficient roles."),
    INSUFFICIENT_PERMISSIONS("You have insufficient permissions."),
    EXPIRED_TOKEN("Your session has expired. Please login again."),
    MISSING_TOKEN("You need to be logged in.");

    String message;

    ErrorType(final String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

  private final ErrorType errorType;
  private final String message;

  public AuthorizationException(final ErrorType errorType) {
    super(errorType.getMessage());
    this.errorType = errorType;
    this.message = null;
  }

  public AuthorizationException(final ErrorType errorType, final String message) {
    super(errorType.getMessage() + " " + message);
    this.errorType = errorType;
    this.message = message;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthorizationException that = (AuthorizationException) o;
    return errorType == that.errorType &&
            Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorType, message);
  }

  @Override
  public String toString() {
    return "AuthorizationException{" +
            "errorType=" + errorType +
            ", message='" + message + '\'' +
            '}';
  }
}
