package com.bwee.springboot.gae.auth.user;

public class AuthUserHolder<T extends AuthUser> {

  private T user;

  public T getUser() {
    return user;
  }

  public AuthUserHolder setUser(final T user) {
    this.user = user;
    return this;
  }
}