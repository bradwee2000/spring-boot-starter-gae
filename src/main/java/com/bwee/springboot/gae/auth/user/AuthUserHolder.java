package com.bwee.springboot.gae.auth.user;

import java.util.Objects;

public class AuthUserHolder<T extends AuthUser> {

    private T user;

    public T getUser() {
        return user;
    }

    public AuthUserHolder setUser(final T user) {
        this.user = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthUserHolder<?> that = (AuthUserHolder<?>) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "AuthUserHolder{" +
                "user=" + user +
                '}';
    }
}