package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.jwt.AuthTokenSigner;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.jwt.TokenStatus;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.AuthUserFactory;
import com.bwee.springboot.gae.auth.user.AuthUserHolder;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author bradwee2000@gmail.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        AuthAutoConfiguration.class,
        AuthIntegrationTest.Ctx.class,
        AuthIntegrationTest.ClassSecuredController.class,
        AuthIntegrationTest.MethodSecuredController.class,
        AuthIntegrationTest.ClassAndMethodSecuredController.class})
@WebMvcTest(secure = false)
@EnableAspectJAutoProxy
public class AuthIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(AuthIntegrationTest.class);

    private static final String INVALID_AUTH_TOKEN = "INVALID.TOKEN";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private SimpleThreadScope simpleThreadScope;

    @Autowired
    private AuthTokenVerifier authTokenVerifier;

    @MockBean
    private PermissionService permissionService;

    @Autowired
    private AuthTokenSigner signer;

    @Autowired
    private AuthUserFactory userFactory;

    @Autowired
    private AuthUserContext userContext;

    private AuthUser user;
    private String token;

    @Before
    public void before() {
        user = userFactory.createUser("XYZ", "John", "tester");
        token = signer.signToken(user);
    }

    @After
    public void after() {
        simpleThreadScope.remove("scopedTarget.authUserHolder");
    }

    @Test
    public void testClassLevelSecured_shouldSecureAllClassMethods() throws Exception {
        mvc.perform(get("/class")).andExpect(status().isForbidden());
        assertThat(userContext.getToken()).isNull();
        assertThat(userContext.getTokenStatus()).isEqualTo(TokenStatus.missing);
        assertThat(userContext.getAuthUser()).isNull();

        mvc.perform(get("/class").header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithValidToken_shouldAllowAccess() throws Exception {
        // with "Bearer" keyword
        mvc.perform(get("/class").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // with no "Bearer" keyword
        mvc.perform(get("/class").header("Authorization", token))
                .andExpect(status().isOk());

        // test class and method secured
        mvc.perform(get("/class-and-method-secured").header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithValidToken_shouldSetUserContext() throws Exception {
        // with "Bearer" keyword
        mvc.perform(get("/class").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThat(userContext.getToken()).isEqualTo(token);
        assertThat(userContext.getTokenStatus()).isEqualTo(TokenStatus.success);
        assertThat(userContext.getAuthUser())
                .extracting("id", "name", "roles")
                .containsExactly("XYZ", "John", singletonList("tester"));
    }

    @Test
    public void testWithInvalidToken_shouldForbidAccess() throws Exception {
        mvc.perform(get("/class/")
                .header("Authorization", "Bearer " + INVALID_AUTH_TOKEN))
                .andExpect(status().isForbidden());
        mvc.perform(get("/class-and-method-secured")
                .header("Authorization", "Bearer " + INVALID_AUTH_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testWithInvalidToken_shouldSetInvalidTokenInUserContext() throws Exception {
        mvc.perform(get("/class/")
                .header("Authorization", "Bearer " + INVALID_AUTH_TOKEN))
                .andExpect(status().isForbidden());

        assertThat(userContext.getToken()).isEqualTo(INVALID_AUTH_TOKEN);
        assertThat(userContext.getTokenStatus()).isEqualTo(TokenStatus.invalid);
        assertThat(userContext.getAuthUser()).isNull();
    }

    @Test
    public void testWithMissingToken_shouldSetMissingTokenInUserContext() throws Exception {
        mvc.perform(get("/method/unsecured")).andExpect(status().isOk());

        assertThat(userContext.getToken()).isNull();
        assertThat(userContext.getTokenStatus()).isEqualTo(TokenStatus.missing);
        assertThat(userContext.getAuthUser()).isNull();

    }

    @Test
    public void testWithGoogleLogin_shouldSetUserContext() throws Exception {
        when(userService.isUserLoggedIn()).thenReturn(Boolean.TRUE);
        when(userService.isUserAdmin()).thenReturn(Boolean.TRUE);
        when(userService.getCurrentUser())
                .thenReturn(new User("test@gmail.com", "tester", "ABC"));

        mvc.perform(get("/method")).andExpect(status().isOk());

        assertThat(userContext.getToken()).isNull();
        assertThat(userContext.getTokenStatus()).isEqualTo(TokenStatus.success);
        assertThat(userContext.getAuthUser().getId()).isEqualTo("ABC");
        assertThat(userContext.getAuthUser().getName()).isEqualTo("test@gmail.com");
        assertThat(userContext.getAuthUser().getRoles()).containsExactly("admin");
    }

    @Test
    public void testMethodSecured_shouldValidateToken() throws Exception {
        // with token
        mvc.perform(get("/method").header("Authorization", token)).andExpect(status().isOk());

        // no token
        mvc.perform(get("/method/")).andExpect(status().isForbidden());
    }

    @Test
    public void testMethodUnsecured_shouldAllowAccess() throws Exception {
        // no token
        mvc.perform(get("/method/unsecured")).andExpect(status().isOk());


        // with valid token
        mvc.perform(get("/method/unsecured").header("Authorization", token))
                .andExpect(status().isOk());

        // with invalid token
        mvc.perform(get("/method/unsecured").header("Authorization", INVALID_AUTH_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    public void testPermissions_shouldCheckUserHasPermissions() throws Exception {
        // test with all permissions available
        when(permissionService.getPermissions(any(Collection.class))).thenReturn(Arrays.asList("Read", "Write"));
        mvc.perform(get("/method/permissions-required")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // test with 1 missing permission
        when(permissionService.getPermissions(any(Collection.class))).thenReturn(Arrays.asList("Read"));
        mvc.perform(get("/method/permissions-required")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Configuration
    public static class Ctx {

        @Bean
        public CustomScopeConfigurer customScopeConfigurer(){
            CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();

            HashMap<String, Object> scopes = new HashMap<>();
            scopes.put(WebApplicationContext.SCOPE_REQUEST, simpleThreadScope());
            scopeConfigurer.setScopes(scopes);
            return scopeConfigurer;
        }

        @Bean
        public SimpleThreadScope simpleThreadScope() {
            return new SimpleThreadScope();
        }
    }

    /**
     * Class-level secured controller.
     */
    @Secured
    @Controller
    @RequestMapping(value = "/class", produces = APPLICATION_JSON_VALUE)
    public static class ClassSecuredController {

        @GetMapping
        public ResponseEntity get() {
            return ResponseEntity.ok("Success");
        }
    }

    /**
     * Method secured controller.
     */
    @Controller
    @RequestMapping(value = "/method", produces = APPLICATION_JSON_VALUE)
    public static class MethodSecuredController {

        @Secured
        @GetMapping
        public ResponseEntity get() {
            return ResponseEntity.ok("Success");
        }

        @Secured("admin")
        @GetMapping("/admin")
        public ResponseEntity getAdminOnly() {
            return ResponseEntity.ok("Success");
        }

        @Secured(permissions = {"Read", "Write"})
        @GetMapping("/permissions-required")
        public ResponseEntity getPermissionRequired() {
            return ResponseEntity.ok("Success");
        }

        @GetMapping("/unsecured")
        public ResponseEntity getUnsecured() {
            return ResponseEntity.ok("Success");
        }
    }

    /**
     * Class and method secured controller.
     */
    @Secured
    @Controller
    @RequestMapping(value = "/class-and-method-secured", produces = APPLICATION_JSON_VALUE)
    public static class ClassAndMethodSecuredController {

        @Secured
        @GetMapping
        public ResponseEntity get() {
            return ResponseEntity.ok("Success");
        }

        @Secured("admin")
        @GetMapping("/admin")
        public ResponseEntity getAdminOnly() {
            return ResponseEntity.ok("Success");
        }

    }
}