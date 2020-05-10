package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
@Aspect
public class AuthHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AuthHandler.class);

    private final AuthUserContext userContext;
    private final PermissionService permissionService;
    private final String adminRole;
    private final String serviceRole;

    public AuthHandler(final AuthUserContext userContext,
                       final PermissionService permissionService,
                       final String adminRole,
                       final String serviceRole) {
        this.userContext = userContext;
        this.permissionService = permissionService;
        this.adminRole = adminRole;
        this.serviceRole = serviceRole;
    }

    @Pointcut("@within(com.bwee.springboot.gae.auth.Secured) || @annotation(Secured)")
    public void method() {
    }

    @Before("method()")
    public void verifyAuthorization(final JoinPoint joinPoint) {
        verifyAuthToken(joinPoint);

        final AuthUser user = userContext.getAuthUser();
        if (user != null) {
            LOG.info("Authenticated: user.id={} user.name={}, user.roles={}", user.getId(), user.getName(), user.getRoles());
            MDC.put("user.id", user.getId());
            MDC.put("user.name", user.getName());
            MDC.put("user.roles", Joiner.on(',').join(user.getRoles()));
        }
    }

    /**
     * Verify that request header contains valid Auth Token.
     */
    private void verifyAuthToken(final JoinPoint joinPoint) {
        if (userContext.getTokenStatus() == null) {
            throw AuthorizationException.missingToken();
        }

        switch (userContext.getTokenStatus()) {
            case missing:
                throw AuthorizationException.missingToken();
            case expired:
                throw AuthorizationException.expiredToken(userContext.getToken());
            case invalid:
                throw AuthorizationException.invalidToken(userContext.getToken());
        }

        final AuthUser user = userContext.getAuthUser();

        if (user.getRoles().contains(adminRole) || user.getRoles().contains(serviceRole)) {
            return;
        }

        // Verify roles and permissions
        final Secured secured = extractAnnotation(joinPoint);
        verifyRoles(secured);
        verifyPermissions(secured);
    }

    /**
     * Check that user has all the required roles
     */
    private void verifyRoles(final Secured secured) {
        final AuthUser user = userContext.getAuthUser();
        final List<String> expected = Lists.newArrayList(secured.value());

        // Must have all required roles
        if (!user.getRoles().containsAll(expected)) {
            final List<String> missing = new ArrayList<>(expected);
            missing.removeAll(user.getRoles());
            throw AuthorizationException.missingRoles(user, missing);
        }
    }

    /**
     * Check that user has all the required permissions
     */
    private void verifyPermissions(final Secured secured) {
        final AuthUser user = userContext.getAuthUser();
        final List<String> expected = Lists.newArrayList(secured.permissions());

        if (expected.isEmpty()) {
            return;
        }

        final List<String> userPermissions = permissionService.getPermissions(user.getRoles());

        if (!userPermissions.containsAll(expected)) {
            final List<String> missing = new ArrayList<>(expected);
            missing.removeAll(userPermissions);
            throw AuthorizationException.missingPermissions(user, missing);
        }
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
