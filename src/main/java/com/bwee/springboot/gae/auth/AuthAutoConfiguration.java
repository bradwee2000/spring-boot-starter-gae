package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class AuthAutoConfiguration {

  @Bean
  public JWTVerifier jwtVerifier(final Algorithm algorithm) {
    return JWT.require(algorithm).build();
  }

  @Bean
  @ConditionalOnMissingBean(Algorithm.class)
  public Algorithm algorithm(@Value("${bwee.jwt.secret.key:secret}") final String secretKey) {
    return Algorithm.HMAC256(secretKey);
  }

  @Bean
  public AuthTokenSigner authTokenSigner(final Algorithm signingAlgorithm,
                                         final TokenTranslator tokenTranslator) {
    return new AuthTokenSigner(signingAlgorithm, tokenTranslator);
  }

  @Bean
  public AuthTokenVerifier authTokenVerifier(final JWTVerifier jwtVerifier,
                                             final TokenTranslator tokenTranslator) {
    return new AuthTokenVerifier(jwtVerifier, tokenTranslator);
  }

  @Bean
  public AuthHandler authHandler(final AuthTokenVerifier authTokenVerifier) {
    return new AuthHandler(authTokenVerifier);
  }

  @Bean
  @ConditionalOnMissingBean(TokenTranslator.class)
  public TokenTranslator tokenTranslator(Clock clock) {
    return new BasicTokenTranslator(clock);
  }

  @Bean
  @ConditionalOnMissingBean(Clock.class)
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
