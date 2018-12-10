package com.bwee.springboot.gae.auth;

import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bradwee2000@gmail.com
 */
@Aspect
public class AuthHandler {
  private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String AUTHORIZED_USER = "AUTH_USER";

  private final AuthTokenVerifier tokenVerifier;

  public AuthHandler(final AuthTokenVerifier tokenVerifier) {
    this.tokenVerifier = tokenVerifier;
  }

  @Before(value = "@annotation(com.bwee.springboot.gae.auth.Secured)")
  public void verifyAuthorization(final JoinPoint joinPoint) {
    final Secured secured = extractAnnotation(joinPoint);
    final List<String> expectedRoles = Lists.newArrayList(secured.value());

    final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();

    checkNotNull(request, "Can only be used in an Http request context.");

    final String token = request.getHeader(AUTHORIZATION_HEADER);

    // Must have valid token
    final VerifiedUser user = (VerifiedUser) tokenVerifier.verifyToken(token);

    // Must have all required roles
    if (!user.getRoles().containsAll(expectedRoles)) {
      throw new AuthorizationException(user, expectedRoles);
    }

    request.setAttribute(AUTHORIZED_USER, user);
  }

  /**
   * Extract the Secured annotation from method or class.
   */
  private Secured extractAnnotation(final JoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();

    final Secured secured = method.getAnnotation(Secured.class) == null ?
        joinPoint.getTarget().getClass().getAnnotation(Secured.class) :
        method.getAnnotation(Secured.class);

    return secured;
  }
}
