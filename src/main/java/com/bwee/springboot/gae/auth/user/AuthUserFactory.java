package com.bwee.springboot.gae.auth.user;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class AuthUserFactory<T extends AuthUser> {

    public T createUser(final String id, final String name, final String role) {
        return createUser(id, name, Collections.singletonList(role));
    }

    public T createUser(final String id, final String name, final String role, final String ... moreRoles) {
        return createUser(id, name, Lists.asList(role, moreRoles));
    }

    public T createUser(final String id, final String name, final List<String> roles) {
        final List<String> filteredRoles = roles == null ?
                emptyList() :
                roles.stream().distinct().sorted().collect(Collectors.toList());

        return (T) new SimpleAuthUser(id, name, filteredRoles);
    }
}
