package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.VerifiedUser;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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

  private final AuthTokenVerifier tokenVerifier;
  private final UserService userService;
  private final AuthUserContext authUserContext;

  public AuthHandler(final AuthTokenVerifier tokenVerifier,
                     final UserService userService,
                     final AuthUserContext authUserContext) {
    this.tokenVerifier = tokenVerifier;
    this.userService = userService;
    this.authUserContext = authUserContext;
  }

  @Pointcut("@within(com.bwee.springboot.gae.auth.Secured) || @annotation(Secured)")
  public void method() {}

  @Before("method()")
  public void verifyAuthorization(final JoinPoint joinPoint) {

    // Allow GAE admins to proceed
    if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
      final User user = userService.getCurrentUser();
      final VerifiedUser verifiedUser = new VerifiedUser(user.getUserId()).name(user.getNickname());
      authUserContext.setAuthUser(verifiedUser);
      return;
    }

    final Secured secured = extractAnnotation(joinPoint);
    final List<String> expectedRoles = Lists.newArrayList(secured.value());

    final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();

    checkNotNull(request, "Can only be used in an Http request context.");

    final String token = StringUtils.replaceChars(request.getHeader(AUTHORIZATION_HEADER), "Bearer ", "");

    if (StringUtils.isEmpty(token)) {
      throw AuthorizationException.missingToken();
    }

    // Must have valid token
    final VerifiedUser user = tokenVerifier.verifyToken(token);

    // Must have all required roles
    if (!user.getRoles().containsAll(expectedRoles)) {
      throw AuthorizationException.missingRoles(user, expectedRoles);
    }

    authUserContext.setAuthUser(user);
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
