package com.bwee.springboot.gae.auth.user;

import com.bwee.springboot.gae.auth.jwt.TokenStatus;

import java.util.Objects;

public class AuthUserHolder<T extends AuthUser> {

    private T user;

    private String token;

    private TokenStatus tokenStatus;

    public T getUser() {
        return user;
    }

    public AuthUserHolder setUser(final T user) {
        this.user = user;
        return this;
    }

    public String getToken() {
        return token;
    }

    public AuthUserHolder<T> setToken(String token) {
        this.token = token;
        return this;
    }

    public TokenStatus getTokenStatus() {
        return tokenStatus;
    }

    public AuthUserHolder<T> setTokenStatus(TokenStatus tokenStatus) {
        this.tokenStatus = tokenStatus;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthUserHolder<?> that = (AuthUserHolder<?>) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(token, that.token) &&
                tokenStatus == that.tokenStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, token, tokenStatus);
    }

    @Override
    public String toString() {
        return "AuthUserHolder{" +
                "user=" + user +
                ", token='" + token + '\'' +
                ", tokenStatus=" + tokenStatus +
                '}';
    }
}