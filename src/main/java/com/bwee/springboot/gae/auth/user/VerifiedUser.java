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
public class VerifiedUser {

  public static VerifiedUser withId(final String id) {
    return new VerifiedUser(id);
  }

  private final String id;
  private final String firstname;
  private final String lastname;
  private final List<String> roles;

  public VerifiedUser(final String id) {
    this(id, null, null, Collections.emptyList());
  }

  public VerifiedUser(String id, String firstname, String lastname, List<String> roles) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.roles = roles;
  }

  public String getId() {
    return id;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public VerifiedUser name(final String firstname) {
    return new VerifiedUser(id, firstname, null, roles);
  }

  public VerifiedUser name(final String firstname, final String lastname) {
    return new VerifiedUser(id, firstname, lastname, roles);
  }

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

  public VerifiedUser roles(final List<String> roles) {
    return new VerifiedUser(id, firstname, lastname, roles);
  }

  public VerifiedUser roles(final String role, final String ... more) {
    return roles(Lists.asList(role, more));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VerifiedUser that = (VerifiedUser) o;
    return id == that.id &&
            Objects.equal(firstname, that.firstname) &&
            Objects.equal(lastname, that.lastname) &&
            Objects.equal(roles, that.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, firstname, lastname, roles);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", firstname + " " + lastname)
            .add("roles", roles)
            .toString();
  }
}
