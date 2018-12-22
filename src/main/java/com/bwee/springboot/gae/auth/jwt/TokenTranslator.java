package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.VerifiedUser;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class TokenTranslator<T extends VerifiedUser> {

  public abstract JWTCreator.Builder toJwt(T t, JWTCreator.Builder jwtBuilder);

  public abstract T decode(DecodedJWT decodedJWT);
}
