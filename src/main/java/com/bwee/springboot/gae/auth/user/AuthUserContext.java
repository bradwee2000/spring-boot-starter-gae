package com.bwee.springboot.gae.auth.user;

import com.bwee.springboot.gae.auth.jwt.TokenStatus;

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

  public String getToken() {
    return contextProvider.get().getToken();
  }

  public AuthUserContext setToken(final String token) {
    contextProvider.get().setToken(token);
    return this;
  }

  public TokenStatus getTokenStatus() {
    return contextProvider.get().getTokenStatus();
  }

  public AuthUserContext setTokenStatus(final TokenStatus tokenStatus) {
    contextProvider.get().setTokenStatus(tokenStatus);
    return this;
  }
}
