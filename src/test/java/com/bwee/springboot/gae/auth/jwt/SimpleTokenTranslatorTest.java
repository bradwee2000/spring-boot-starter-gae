package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.VerifiedUser;
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
public class SimpleTokenTranslatorTest {

  private SimpleTokenTranslator translator = new SimpleTokenTranslator(Clock.systemDefaultZone());

  @Test
  public void testDecode_shouldReturnVerifiedUser() {
    final DecodedJWT decoded = mock(DecodedJWT.class, RETURNS_DEEP_STUBS);
    when(decoded.getSubject()).thenReturn("123");
    when(decoded.getClaim("fn").asString()).thenReturn("John");
    when(decoded.getClaim("fn").asString()).thenReturn("John");
    when(decoded.getClaim("ln").asString()).thenReturn("Doe");
    when(decoded.getClaim("rl").asList(String.class)).thenReturn(Lists.newArrayList("Role1", "Role2"));

    final VerifiedUser user = translator.decode(decoded);
    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo("123");
    assertThat(user.getFirstname()).isEqualTo("John");
    assertThat(user.getLastname()).isEqualTo("Doe");
    assertThat(user.getRoles()).containsExactly("Role1", "Role2");
  }
}