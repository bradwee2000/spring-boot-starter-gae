package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.exception.AuthorizationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface PermissionService {

    Map<String, List<String>> getAll();

    List<String> getRoles();

    List<String> getPermissions(final Collection<String> roles);

    default List<String> getPermissions(final String role) {
        return getPermissions(Collections.singleton(role));
    }

    boolean hasPermission(final String permission);

    default void hasPermissionOrThrow(final String permission) {
        if (!hasPermission(permission)) {
            throw new AuthorizationException(
                    AuthorizationException.ErrorType.INSUFFICIENT_RIGHTS,
                    "Missing permission: " + permission);
        }
    };

    void deleteRole(final String role);

    void saveRole(final String role, final Collection<String> permissions);
}
