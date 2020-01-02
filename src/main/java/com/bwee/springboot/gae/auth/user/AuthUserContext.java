package com.bwee.springboot.gae.auth.user;

import javax.inject.Provider;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthUserContext<T extends AuthUser> {

  private final Provider<AuthUserHolder<T>> contextProvider;

  public AuthUserContext(final Provider<AuthUserHolder<T>> contextProvider) {
    this.contextProvider = contextProvider;
  }

  public T getAuthUser() {
    return contextProvider.get().getUser();
  }

  public AuthUserContext setAuthUser(final T user) {
    contextProvider.get().setUser(user);
    return this;
  }
}
