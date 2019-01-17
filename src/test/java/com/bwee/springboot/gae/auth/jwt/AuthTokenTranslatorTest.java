package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.AuthUser;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenTranslatorTest {

  private AuthTokenTranslator translator = new AuthTokenTranslator(Clock.systemDefaultZone());

  @Test
  public void testDecode_shouldReturnVerifiedUser() {
    final DecodedJWT decoded = mock(DecodedJWT.class, RETURNS_DEEP_STUBS);
    when(decoded.getSubject()).thenReturn("123");
    when(decoded.getClaim("nm").asString()).thenReturn("John");
    when(decoded.getClaim("rl").asList(String.class)).thenReturn(Lists.newArrayList("Role1", "Role2"));

    final AuthUser user = translator.decode(decoded);
    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo("123");
    assertThat(user.getName()).isEqualTo("John");
    assertThat(user.getRoles()).containsExactly("Role1", "Role2");
  }
}