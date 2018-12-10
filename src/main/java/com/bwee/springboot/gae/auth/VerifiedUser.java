package com.bwee.springboot.gae.auth;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public class VerifiedUser {

  private final String id;
  private final String firstname;
  private final String lastname;
  private final List<String> roles;

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

  public List<String> getRoles() {
    return roles;
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
            .add("firstname", firstname)
            .add("lastname", lastname)
            .add("roles", roles)
            .toString();
  }
}
