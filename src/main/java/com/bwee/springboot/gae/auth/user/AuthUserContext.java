package com.bwee.springboot.gae.auth.user;

import javax.inject.Provider;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthUserContext {

  private final Provider<AuthUserHolder> contextProvider;

  public AuthUserContext(Provider<AuthUserHolder> contextProvider) {
    this.contextProvider = contextProvider;
  }

  public VerifiedUser getAuthUser() {
    return contextProvider.get().getUser();
  }

  public void setAuthUser(final VerifiedUser user) {
    contextProvider.get().setUser(user);
  }
}
