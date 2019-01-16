package com.bwee.springboot.gae.auth.user;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public class SimpleAuthUser implements AuthUser {

  public static SimpleAuthUser withId(final String id) {
    return new SimpleAuthUser(id);
  }

  private final String id;
  private final String name;
  private final List<String> roles;

  public SimpleAuthUser(final String id) {
    this(id, null, Collections.emptyList());
  }

  public SimpleAuthUser(String id, String name, List<String> roles) {
    this.id = id;
    this.name = name;
    this.roles = roles;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  public SimpleAuthUser name(final String name) {
    return new SimpleAuthUser(id, name , roles);
  }

  @Override
  public List<String> getRoles() {
    return roles;
  }

  public boolean hasAnyRole(final String role, final String ... more) {
    return hasAnyRole(Lists.asList(role, more));
  }

  public boolean hasAnyRole(final Collection<String> roles) {
    return this.roles.stream().filter(r -> roles.contains(r)).findFirst().isPresent();
  }

  public boolean hasAllRoles(final String role, final String ... more) {
    return hasAllRoles(Lists.asList(role, more));
  }

  public boolean hasAllRoles(final Collection<String> roles) {
    return this.roles.containsAll(roles);
  }

  public SimpleAuthUser roles(final List<String> roles) {
    return new SimpleAuthUser(id, name, roles);
  }

  public SimpleAuthUser roles(final String role, final String ... more) {
    return roles(Lists.asList(role, more));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SimpleAuthUser that = (SimpleAuthUser) o;
    return id == that.id &&

            Objects.equal(name, that.name) &&
            Objects.equal(roles, that.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, roles);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .add("roles", roles)
            .toString();
  }
}
