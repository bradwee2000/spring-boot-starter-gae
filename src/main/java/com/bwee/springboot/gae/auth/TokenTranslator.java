package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class TokenTranslator<T extends VerifiedUser> {

  public abstract JWTCreator.Builder toJwt(T t, JWTCreator.Builder jwtBuilder);

  public abstract T decode(DecodedJWT decodedJWT);
}
