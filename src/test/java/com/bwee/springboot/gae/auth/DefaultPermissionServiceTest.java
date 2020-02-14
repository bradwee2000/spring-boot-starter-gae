package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.model.service.ConfigService;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultPermissionServiceTest {

    private DefaultPermissionService service;

    private ConfigService configService;
    private AuthUserContext<AuthUser> userContext;
    private ConfigService.PropertyConverter converter;
    private AuthUser user;
    private Map<String, List<String>> roles = ImmutableMap.of(
            "admin", asList("insert", "update", "delete", "view"),
            "viewer", asList("view"));

    @Before
    public void before() {
        configService = mock(ConfigService.class);
        userContext = mock(AuthUserContext.class);
        converter = mock(ConfigService.PropertyConverter.class);
        service = new DefaultPermissionService(configService, userContext);
        user = mock(AuthUser.class);

        when(configService.getProperty(anyString())).thenReturn(converter);
        when(converter.asType(any())).thenReturn(Optional.of(roles));
        when(userContext.getAuthUser()).thenReturn(user);
        when(user.getRoles()).thenReturn(asList("viewer"));
    }

    @Test
    public void testGetRoles_shouldReturnAllRoles() {
        assertThat(service.getRoles()).containsExactlyInAnyOrder("admin", "viewer");
    }

    @Test
    public void testGetAll_shouldReturnAllRolesAndTheirPermissions() {
        assertThat(service.getAll()).isEqualTo(roles);
    }

    @Test
    public void testGetPermissions_shouldReturnPermissionsAssignedToRoles() {
        assertThat(service.getPermissions("admin"))
                .containsExactlyInAnyOrder("insert", "update", "delete", "view");
        assertThat(service.getPermissions("viewer"))
                .containsExactlyInAnyOrder("view");
        assertThat(service.getPermissions(asList("admin", "viewer", "UNKNOWN")))
                .containsOnlyOnce("insert", "update", "delete", "view");
    }

    @Test
    public void testHasPermission_shouldReturnTrueIfUserHasRoleContainingPermission() {
        assertThat(service.hasPermission("view")).isTrue();
        assertThat(service.hasPermission("update")).isFalse();
    }
}