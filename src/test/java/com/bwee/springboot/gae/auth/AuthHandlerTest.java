package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.jwt.AuthTokenTranslator;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.SimpleAuthUser;
import com.google.appengine.api.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author bradwee2000@gmail.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AuthHandlerTest.Ctx.class, })
@WebMvcTest(value = {
        AuthHandlerTest.ClassSecuredController.class,
        AuthHandlerTest.MethodSecuredController.class,
        AuthHandlerTest.ClassAndMethodSecuredController.class
},
    secure = false)
public class AuthHandlerTest {
  private static final Logger LOG = LoggerFactory.getLogger(AuthHandlerTest.class);

  private static final String VALID_AUTH_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJZdHpIRnBVYSIs" +
      "ImZuIjoiSm9obiBEb2UiLCJybCI6WyJzYWxlcyJdLCJqdGkiOiJZdHpIRnBVYS0yMDE4LTEyLTI0VDE2OjE1OjE5Ljc5MyJ9.C4wLe-7" +
      "SHdbzmm6BAWMiGsisrdchUa4knSPfhB3lvJQ";
  private static final String INVALID_AUTH_TOKEN = "INVALID.TOKEN";

  @Autowired
  private MockMvc mvc;

  @Autowired
  private AuthTokenVerifier authTokenVerifier;

  @Autowired
  private PermissionService permissionService;

  @Before
  public void before() {
    reset(authTokenVerifier);

    doThrow(AuthorizationException.invalidToken("Invalid Token")).when(authTokenVerifier).verifyToken(any());

    doReturn(SimpleAuthUser.withId("123").name("John")).when(authTokenVerifier).verifyToken(VALID_AUTH_TOKEN);
  }

  @Test
  public void testClassLevelSecured_shouldSecureAllClassMethods() throws Exception {
    mvc.perform(get("/class/")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testWithInvalidToken_shouldForbidAccess() throws Exception {
    mvc.perform(get("/class/").header("Authorization", "Bearer " + INVALID_AUTH_TOKEN))
        .andExpect(status().isForbidden());
    verify(authTokenVerifier).verifyToken(INVALID_AUTH_TOKEN);
  }

  @Test
  public void testWithValidToken_shouldAllowAccess() throws Exception {
    mvc.perform(get("/class/").header("Authorization", "Bearer " + VALID_AUTH_TOKEN)).andExpect(status().isOk());
    mvc.perform(get("/class/").header("Authorization", VALID_AUTH_TOKEN)).andExpect(status().isOk());
  }

  @Test
  public void testMethodSecured_shouldSecureMethod() throws Exception {
    mvc.perform(get("/method/")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testMethodUnsecured_shouldAllowAccess() throws Exception {
    mvc.perform(get("/method/unsecured")).andExpect(status().isOk());
  }

  @Test
  public void testPermissions_shouldCheckUserHasPermissions() throws Exception {
    // test with all permissions available
    when(permissionService.getPermissions(any(Collection.class))).thenReturn(Arrays.asList("Read", "Write"));
    mvc.perform(get("/method/permissions-required")
            .header("Authorization", "Bearer " + VALID_AUTH_TOKEN))
            .andExpect(status().isOk());

    // test with 1 missing permission
    when(permissionService.getPermissions(any(Collection.class))).thenReturn(Arrays.asList("Read"));
    mvc.perform(get("/method/permissions-required")
            .header("Authorization", "Bearer " + VALID_AUTH_TOKEN))
            .andExpect(status().isForbidden());
  }

  @Test
  public void testWithClassAndMethodSecured_shouldProcessOnce() throws Exception {
    mvc.perform(get("/class-and-method-secured")
            .header("Authorization", INVALID_AUTH_TOKEN))
            .andExpect(status().isForbidden());
    verify(authTokenVerifier).verifyToken(INVALID_AUTH_TOKEN);
  }

  @Configuration
  @EnableAspectJAutoProxy
  public static class Ctx {
    @Bean
    public AuthTokenVerifier authTokenVerifier() {
      return mock(AuthTokenVerifier.class);
    }

    @Bean
    public AuthTokenTranslator authTokenTranslator() {
      return mock(AuthTokenTranslator.class);
    }

    @Bean
    public UserService userService() {
      return mock(UserService.class);
    }

    @Bean
    public AuthUserContext authUserContext() {
      return mock(AuthUserContext.class);
    }

    @Bean
    public ClassSecuredController classSecuredController() {
      return new ClassSecuredController();
    }

    @Bean
    public MethodSecuredController methodSecuredController() {
      return new MethodSecuredController();
    }

    @Bean
    public ClassAndMethodSecuredController classAndMethodSecuredController() {
      return new ClassAndMethodSecuredController();
    }

    @Bean
    public PermissionService permissionProvider() {
      return mock(PermissionService.class);
    }

    @Bean
    public AuthHandler authHandler(final AuthTokenVerifier authTokenVerifier,
                                   final UserService userService,
                                   final AuthUserContext authUserContext,
                                   final AuthTokenTranslator authTokenTranslator,
                                   final PermissionService permissionService) {
      return new AuthHandler(authTokenVerifier, userService, authUserContext, authTokenTranslator, permissionService,
              "admin", "service");
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

    @Secured("ADMIN")
    @GetMapping
    public ResponseEntity get() {
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

    @Secured("ADMIN")
    @GetMapping
    public ResponseEntity get() {
      return ResponseEntity.ok("Success");
    }

  }
}