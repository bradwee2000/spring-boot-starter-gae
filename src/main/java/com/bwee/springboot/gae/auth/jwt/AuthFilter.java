package com.bwee.springboot.gae.auth.jwt;

import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.AuthUserFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class AuthFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TASK_NAME_HEADER = "X-AppEngine-TaskName";

    private final AuthUserContext userContext;
    private final AuthTokenVerifier tokenVerifier;
    private final AuthTokenTranslator tokenTranslator;
    private final AuthUserFactory userFactory;
    private final UserService userService;
    private final String adminRole;
    private final String serviceRole;

    public AuthFilter(final AuthUserContext userContext,
                      final AuthTokenVerifier tokenVerifier,
                      final AuthTokenTranslator tokenTranslator,
                      final AuthUserFactory userFactory,
                      final UserService userService,
                      final String adminRole,
                      final String serviceRole) {
        this.userContext = userContext;
        this.tokenVerifier = tokenVerifier;
        this.tokenTranslator = tokenTranslator;
        this.userFactory = userFactory;
        this.userService = userService;
        this.adminRole = adminRole;
        this.serviceRole = serviceRole;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest req = (HttpServletRequest) servletRequest;

        if (!checkIsAdmin() && !checkIsSystem()) {
            checkToken(req);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Check if request is from logged in admin.
     */
    private boolean checkIsAdmin() {
        if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
            final User user = userService.getCurrentUser();
            final AuthUser verifiedUser = userFactory.createUser(user.getUserId(), user.getNickname(), adminRole);
            userContext.setAuthUser(verifiedUser).setTokenStatus(TokenStatus.success);
            return true;
        }
        return false;
    }

    /**
     * Check if request is from system service.
     */
    private boolean checkIsSystem() {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        checkNotNull(request, "Can only be used in an Http request context.");

        if (!StringUtils.isEmpty(request.getHeader(TASK_NAME_HEADER))) {
            final String taskName = request.getHeader(TASK_NAME_HEADER);
            final AuthUser authUser = userFactory.createUser(taskName, "system", serviceRole);
            userContext.setAuthUser(authUser);
            return true;
        }
        return false;
    }

    private void checkToken(final HttpServletRequest req) {
        final String token = StringUtils.replace(req.getHeader(AUTHORIZATION_HEADER),
                "Bearer ", "");
        userContext.setToken(token);

        if (StringUtils.isEmpty(token)) {
            userContext.setTokenStatus(TokenStatus.missing);
        } else {
            try {
                final AuthUser user = tokenVerifier.verifyToken(token);
                userContext.setAuthUser(user).setTokenStatus(TokenStatus.success);
            } catch (final AuthorizationException e) {
                // Do not throw errors yet. Not all URLs need security.
                switch (e.getErrorType()) {
                    case EXPIRED_TOKEN: userContext.setTokenStatus(TokenStatus.expired); break;
                    case INVALID_TOKEN: userContext.setTokenStatus(TokenStatus.invalid); break;
                }
            }
        }
    }
}
