package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.bwee.springboot.gae.auth.jwt.AuthFilter;
import com.bwee.springboot.gae.auth.jwt.AuthTokenSigner;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.jwt.AuthTokenTranslator;
import com.bwee.springboot.gae.auth.jwt.TokenTranslator;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.AuthUserFactory;
import com.bwee.springboot.gae.auth.user.AuthUserHolder;
import com.bwee.springboot.gae.model.ConfigAutoConfiguration;
import com.bwee.springboot.gae.model.service.ConfigService;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.annotation.RequestScope;

import javax.inject.Provider;
import java.time.Clock;
import java.util.Collections;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
@Import(ConfigAutoConfiguration.class)
public class AuthAutoConfiguration {

  @Value("${bwee.role.admin:admin}")
  private String adminRole;

  @Value("${bwee.role.service:system}")
  private String serviceRole;

  @Bean
  @ConditionalOnMissingBean(JWTVerifier.class)
  public JWTVerifier jwtVerifier(final Algorithm algorithm) {
    return JWT.require(algorithm).build();
  }

  @Bean
  @ConditionalOnMissingBean(Algorithm.class)
  public Algorithm algorithm(@Value("${bwee.jwt.secret.key:secret}") final String secretKey) {
    return Algorithm.HMAC256(secretKey);
  }

  @Bean
  @ConditionalOnMissingBean(AuthTokenSigner.class)
  public AuthTokenSigner authTokenSigner(final Algorithm signingAlgorithm,
                                         final TokenTranslator tokenTranslator) {
    return new AuthTokenSigner(signingAlgorithm, tokenTranslator);
  }

  @Bean
  @ConditionalOnMissingBean(AuthTokenVerifier.class)
  public AuthTokenVerifier authTokenVerifier(final JWTVerifier jwtVerifier,
                                             final TokenTranslator tokenTranslator) {
    return new AuthTokenVerifier(jwtVerifier, tokenTranslator);
  }

  @Bean
  @ConditionalOnMissingBean(AuthHandler.class)
  public AuthHandler authHandler(final AuthUserContext authUserContext,
                                 final PermissionService permissionService) {
    return new AuthHandler(authUserContext, permissionService, adminRole, serviceRole);
  }

  @Bean
  @ConditionalOnMissingBean(TokenTranslator.class)
  public AuthTokenTranslator tokenTranslator(final Clock clock, final AuthUserFactory userFactory) {
    return new AuthTokenTranslator(clock, userFactory);
  }

  @Bean
  @ConditionalOnMissingBean(AuthUserFactory.class)
  public AuthUserFactory authUserFactory() {
    return new AuthUserFactory();
  }

  @Bean
  @ConditionalOnMissingBean(Clock.class)
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  @ConditionalOnMissingBean(UserService.class)
  public UserService userService() {
    return UserServiceFactory.getUserService();
  }

  @Bean
  @ConditionalOnMissingBean(AuthUserContext.class)
  public AuthUserContext authUserContext(final Provider<AuthUserHolder> authUserHolder) {
    return new AuthUserContext(authUserHolder);
  }

  @Bean
  @ConditionalOnMissingBean(PermissionService.class)
  public PermissionService permissionService(final ConfigService configService,
                                             final AuthUserContext<? extends AuthUser> userContext) {
    return new DefaultPermissionService(configService, userContext);
  }

  @Bean
  @RequestScope
  @ConditionalOnMissingBean(AuthUserHolder.class)
  public AuthUserHolder authUserHolder() {
    return new AuthUserHolder();
  }

  @Bean
  @Order(1)
  @ConditionalOnMissingBean(AuthFilter.class)
  public AuthFilter authFilter(final AuthUserContext userContext,
                               final AuthTokenVerifier tokenVerifier,
                               final AuthTokenTranslator tokenTranslator,
                               final AuthUserFactory userFactory,
                               final UserService userService) {
    return new AuthFilter(userContext, tokenVerifier, tokenTranslator, userFactory, userService, adminRole, serviceRole);
  }
}
