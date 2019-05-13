package com.bwee.springboot.gae.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface PermissionProvider {

    List<String> getPermissions(final Collection<String> roles);

    default List<String> getPermissions(final String role) {
        return getPermissions(Collections.singleton(role));
    }
}
