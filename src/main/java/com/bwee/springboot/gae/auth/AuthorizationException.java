package com.bwee.springboot.gae.auth;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static com.bwee.springboot.gae.auth.AuthorizationException.ErrorType.EXPIRED_TOKEN;
import static com.bwee.springboot.gae.auth.AuthorizationException.ErrorType.INSUFFICIENT_RIGHTS;
import static com.bwee.springboot.gae.auth.AuthorizationException.ErrorType.INVALID_TOKEN;
import static com.bwee.springboot.gae.auth.AuthorizationException.ErrorType.MISSING_TOKEN;

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

  public static AuthorizationException missingRoles(final VerifiedUser verifiedUser,
                                                    final Collection<String> requiredRoles) {
    final String msg = "User " + verifiedUser + " has insufficient roles. Required: "
        + Joiner.on(", ").join(requiredRoles);
    return new AuthorizationException(INSUFFICIENT_RIGHTS, msg);
  }

  public enum ErrorType {
    MISSING_TOKEN("Missing access token."),
    INVALID_TOKEN("Invalid access token."),
    INSUFFICIENT_RIGHTS("User has insufficient rights."),
    EXPIRED_TOKEN("Expired access token.");

    String message;

    ErrorType(final String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

  private final ErrorType errorType;

  public AuthorizationException(final ErrorType errorType) {
    super(errorType.getMessage());
    this.errorType = errorType;
  }

  public AuthorizationException(final ErrorType errorType, final String message) {
    super(errorType.getMessage() + " " + message);
    this.errorType = errorType;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthorizationException that = (AuthorizationException) o;
    return errorType == that.errorType;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(errorType);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("errorType", errorType)
        .toString();
  }
}
