package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.exception.AuthenticationException;
import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.jwt.AuthTokenTranslator;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
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
    private static final String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    private final AuthTokenVerifier tokenVerifier;
    private final UserService userService;
    private final AuthUserContext authUserContext;
    private final AuthTokenTranslator tokenTranslator;
    private final PermissionService permissionService;
    private final String adminRole;
    private final String serviceRole;

    public AuthHandler(final AuthTokenVerifier tokenVerifier,
                       final UserService userService,
                       final AuthUserContext authUserContext,
                       final AuthTokenTranslator tokenTranslator,
                       final PermissionService permissionService,
                       final String adminRole,
                       final String serviceRole) {
        this.tokenVerifier = tokenVerifier;
        this.userService = userService;
        this.authUserContext = authUserContext;
        this.tokenTranslator = tokenTranslator;
        this.permissionService = permissionService;
        this.adminRole = adminRole;
        this.serviceRole = serviceRole;
    }

    @Pointcut("@within(com.bwee.springboot.gae.auth.Secured) || @annotation(Secured)")
    public void method() {
    }

    @Before("method()")
    public void verifyAuthorization(final JoinPoint joinPoint) {
        // Allow GAE services to proceed
        if (checkIsService()) {
            return;
        }

        // Allow GAE admins to proceed
        if (checkIsAdmin()) {
            return;
        }

        verifyAuthToken(joinPoint);

        final AuthUser user = authUserContext.getAuthUser();
        if (user != null) {
            LOG.info("Requested by user.id={}, user.name={}, user.roles={}",
                    user.getId(), user.getName(), user.getRoles());
        }
    }

    /**
     * Check if request is from logged in admin.
     */
    private boolean checkIsAdmin() {
        if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
            final User user = userService.getCurrentUser();
            final AuthUser verifiedUser = tokenTranslator.createUser(user.getUserId(), user.getNickname(), adminRole);
            authUserContext.setAuthUser(verifiedUser);
            return true;
        }
        return false;
    }

    /**
     * Check if request is from system service.
     */
    private boolean checkIsService() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        checkNotNull(request, "Can only be used in an Http request context.");

        if (!StringUtils.isEmpty(request.getHeader(TASK_NAME_HEADER))) {
            final String taskName = request.getHeader(TASK_NAME_HEADER);
            final AuthUser authUser = tokenTranslator.createUser(taskName, "system", serviceRole);
            authUserContext.setAuthUser(authUser);
            return true;
        }
        return false;
    }

    /**
     * Verify that request header contains valid Auth Token.
     */
    private void verifyAuthToken(final JoinPoint joinPoint) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        checkNotNull(request, "Can only be used in an Http request context.");

        final String token = StringUtils.replace(request.getHeader(AUTHORIZATION_HEADER), "Bearer ", "");

        if (StringUtils.isEmpty(token)) {
            throw AuthenticationException.missingToken();
        }

        // Must have valid token
        final AuthUser user = tokenVerifier.verifyToken(token);

        // Verify roles and permissions
        final Secured secured = extractAnnotation(joinPoint);
        verifyRoles(secured, user);
        verifyPermissions(secured, user);

        authUserContext.setAuthUser(user);
    }

    /**
     * Check that user has all the required roles
     */
    private void verifyRoles(final Secured secured, final AuthUser user) {
        final List<String> expectedRoles = Lists.newArrayList(secured.value());

        // Must have all required roles
        if (!user.getRoles().containsAll(expectedRoles)) {
            throw AuthorizationException.missingRoles(user);
        }
    }

    /**
     * Check that user has all the required permissions
     */
    private void verifyPermissions(final Secured secured, final AuthUser user) {
        final List<String> expectedPermissions = Lists.newArrayList(secured.permissions());

        if (expectedPermissions.isEmpty()) {
            return;
        }

        final List<String> userPermissions = permissionService.getPermissions(user.getRoles());

        if (!userPermissions.containsAll(expectedPermissions)) {
            throw AuthorizationException.missingPermissions(user);
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
