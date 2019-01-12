package com.bwee.springboot.gae.auth.user;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author bradwee2000@gmail.com
 */
public class VerifiedUserTest {

  private VerifiedUser user = new VerifiedUser("testId").roles("Admin", "Sales");

  @Test
  public void testHasAnyRole_shouldReturnTrueIfContainsAnyRole() {
    assertThat(user.hasAnyRole("Admin")).isTrue();
    assertThat(user.hasAnyRole("Admin", "Xyz")).isTrue();
    assertThat(user.hasAnyRole("Sales", "Xyz")).isTrue();
    assertThat(user.hasAnyRole("Sales", "Admin")).isTrue();
    assertThat(user.hasAnyRole("XXX", "YYY")).isFalse();
  }

  @Test
  public void testHasAllRoles_shouldReturnTrueIfContainsAllRoles() {
    assertThat(user.hasAllRoles("Admin")).isTrue();
    assertThat(user.hasAllRoles("Sales")).isTrue();
    assertThat(user.hasAllRoles("Sales", "Admin")).isTrue();
    assertThat(user.hasAllRoles("Sales", "Xyz")).isFalse();
    assertThat(user.hasAllRoles("XXX", "YYY")).isFalse();
    assertThat(user.hasAllRoles("Admin", "Xyz")).isFalse();
  }
}