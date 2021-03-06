package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.AuthUser;

/**
 * @author bradwee2000@gmail.com
 */
public interface TokenTranslator<T extends AuthUser> {

  JWTCreator.Builder toJwt(T t, JWTCreator.Builder jwtBuilder);

  T decode(DecodedJWT decodedJWT);
}
