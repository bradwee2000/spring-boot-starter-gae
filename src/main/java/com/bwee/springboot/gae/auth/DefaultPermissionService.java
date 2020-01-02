package com.bwee.springboot.gae.auth;

import com.bwee.springboot.gae.auth.user.AuthUser;
import com.bwee.springboot.gae.auth.user.AuthUserContext;
import com.bwee.springboot.gae.model.service.ConfigService;
import com.bwee.springboot.gae.model.service.MapStoreService;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class DefaultPermissionService extends MapStoreService<String, List<String>> implements PermissionService {
    private static final String NAMESPACE = "roles.permissions";
    private static final Type TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();

    private final AuthUserContext<? extends AuthUser> userContext;

    @Autowired
    public DefaultPermissionService(final ConfigService configService,
                                    final AuthUserContext<? extends AuthUser> userContext) {
        super(NAMESPACE, TYPE, configService);
        this.userContext = userContext;
    }

    @Override
    public List<String> getRoles() {
        return getAll().keySet().stream().sorted().collect(toList());
    }

    @Override
    public List<String> getPermissions(final Collection<String> roles) {
        return getAll().entrySet().stream()
                .filter(e -> roles.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .distinct()
                .collect(toList());
    }

    @Override
    public boolean hasPermission(final String permission) {
        final Collection<String> roles = userContext.getAuthUser().getRoles();
        return roles.contains(permission);
    }

    @Override
    public void deleteRole(final String role) {
        remove(role);
    }

    @Override
    public void saveRole(final String role, final Collection<String> permissions) {
        put(role, new ArrayList(permissions));
    }
}
