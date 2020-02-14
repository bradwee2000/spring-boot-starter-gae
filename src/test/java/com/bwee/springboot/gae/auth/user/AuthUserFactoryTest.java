package com.bwee.springboot.gae.auth.user;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AuthUserFactoryTest {

    private AuthUserFactory factory;

    @Before
    public void before() {
        factory = new AuthUserFactory();
    }

    @Test
    public void testCreateUser_shouldReturnUser() {
        final AuthUser user = factory.createUser("1", "John", "Admin");

        assertThat(user).isNotNull();
        assertThat(user).extracting(u -> u.getId(), u -> u.getName(), u -> u.getRoles())
                .doesNotContainNull()
                .containsExactly("1", "John", Arrays.asList("Admin"));
    }

    @Test
    public void testCreateWithDuplicateRoles_shouldRemoveDuplicates() {
        final AuthUser user = factory.createUser(
                "1", "John", "Admin", "Owner", "Admin", "Tester", "Sales");

        assertThat(user).isNotNull();
        assertThat(user).extracting(u -> u.getId(), u -> u.getName(), u -> u.getRoles())
                .doesNotContainNull()
                .containsExactly("1", "John", Arrays.asList("Admin", "Owner", "Sales", "Tester"));
    }

}