package com.bwee.springboot.gae.auth;

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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author bradwee2000@gmail.com
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AuthHandlerTest.Ctx.class, })
@WebMvcTest(value = {AuthHandlerTest.ClassSecuredController.class,
    AuthHandlerTest.MethodSecuredController.class},
    secure = false)
public class AuthHandlerTest {
  private static final Logger LOG = LoggerFactory.getLogger(AuthHandlerTest.class);

  @Autowired
  private MockMvc mvc;

  @Autowired
  private AuthTokenVerifier authTokenVerifier;

  @Test
  public void testClassLevelSecured_shouldSecureAllClassMethods() throws Exception {
    mvc.perform(get("/class/")).andExpect(status().isForbidden());
  }

  @Test
  public void testWithInvalidToken_shouldForbidAccess() throws Exception {
    when(authTokenVerifier.verifyToken("INVALID")).thenThrow(AuthorizationException.invalidToken("Invalid Token"));
    mvc.perform(get("/class/").header("Authorization", "Bearer INVALID")).andExpect(status().isForbidden());
  }

  @Test
  public void testWithValidToken_shouldAllowAccess() throws Exception {
    when(authTokenVerifier.verifyToken("VALID")).thenReturn(VerifiedUser.withId("123").name("John", "Doe"));
    mvc.perform(get("/class/").header("Authorization", "Bearer VALID")).andExpect(status().isOk());
    mvc.perform(get("/class/").header("Authorization", "VALID")).andExpect(status().isOk());
  }

  @Test
  public void testMethodSecured_shouldSecureMethod() throws Exception {
    mvc.perform(get("/method/")).andExpect(status().isForbidden());
  }

  @Test
  public void testMethodUnsecured_shouldAllowAccess() throws Exception {
    mvc.perform(get("/method/unsecured")).andExpect(status().isOk());
  }

  @Configuration
  @EnableAspectJAutoProxy
  public static class Ctx {
    @Bean
    public AuthTokenVerifier authTokenVerifier() {
      return mock(AuthTokenVerifier.class);
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
    public AuthHandler authHandler(final AuthTokenVerifier authTokenVerifier) {
      return new AuthHandler(authTokenVerifier);
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

    @GetMapping("/unsecured")
    public ResponseEntity getUnsecured() {
      return ResponseEntity.ok("Success");
    }
  }
}