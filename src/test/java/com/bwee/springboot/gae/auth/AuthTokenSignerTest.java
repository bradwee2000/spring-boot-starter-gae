package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.bwee.springboot.gae.auth.jwt.AuthTokenSigner;
import com.bwee.springboot.gae.auth.jwt.TokenTranslator;
import com.bwee.springboot.gae.auth.user.VerifiedUser;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenSignerTest {

  private final LocalDateTime now = LocalDateTime.now();
  private final Clock clock = Clock.fixed(now.toInstant(OffsetDateTime.now().getOffset()), ZoneId.systemDefault());
  private final Algorithm algorithm = Algorithm.HMAC256("dth6MC2pyMdgF7CHtcr93YgBt");

  private VerifiedUser verifiedUser;
  private AuthTokenSigner tokenSigner;
  private TokenTranslator tokenTranslator;
  private JWTCreator.Builder jwtBuilder;

  @Before
  public void before() {
    tokenTranslator = mock(TokenTranslator.class);
    jwtBuilder = mock(JWTCreator.Builder.class);
    verifiedUser = VerifiedUser.withId("xyz").name("John", "Doe").roles("Viewer");

    when(tokenTranslator.toJwt(eq(verifiedUser), any())).thenReturn(jwtBuilder);
    when(jwtBuilder.sign(algorithm)).thenReturn("success");

    tokenSigner = new AuthTokenSigner(algorithm, tokenTranslator);
  }

  @Test
  public void testGenerateToken_shouldReturnSignedToken() {
    final String token = tokenSigner.signToken(verifiedUser);
    assertThat(token).isEqualTo("success");
  }
}