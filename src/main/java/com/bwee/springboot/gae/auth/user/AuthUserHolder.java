package com.bwee.springboot.gae.auth.user;

public class AuthUserHolder {

  private VerifiedUser user;

  public VerifiedUser getUser() {
    return user;
  }

  public AuthUserHolder setUser(final VerifiedUser user) {
    this.user = user;
    return this;
  }
}