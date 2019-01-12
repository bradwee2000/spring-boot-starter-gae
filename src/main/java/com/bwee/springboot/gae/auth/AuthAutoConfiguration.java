package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.bwee.springboot.gae.auth.jwt.AuthTokenSigner;
import com.bwee.springboot.gae.auth.jwt.AuthTokenVerifier;
import com.bwee.springboot.gae.auth.jwt.SimpleTokenTranslator;
import com.bwee.springboot.gae.auth.jwt.TokenTranslator;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.auth.user.AuthUserHolder;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import javax.inject.Provider;
import java.time.Clock;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class AuthAutoConfiguration {

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
  public AuthHandler authHandler(final AuthTokenVerifier authTokenVerifier,
                                 final UserService userService,
                                 final AuthUserContext authUserContext) {
    return new AuthHandler(authTokenVerifier, userService, authUserContext);
  }

  @Bean
  @ConditionalOnMissingBean(TokenTranslator.class)
  public TokenTranslator tokenTranslator(final Clock clock) {
    return new SimpleTokenTranslator(clock);
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
  @RequestScope
  @ConditionalOnMissingBean(AuthUserHolder.class)
  public AuthUserHolder authUserHolder() {
    return new AuthUserHolder();
  }
}
