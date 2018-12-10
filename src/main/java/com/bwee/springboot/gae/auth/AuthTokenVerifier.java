package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenVerifier {

  private final JWTVerifier jwtVerifier;
  private final TokenTranslator tokenTranslator;

  public AuthTokenVerifier(final JWTVerifier jwtVerifier, final TokenTranslator tokenTranslator) {
    this.jwtVerifier = jwtVerifier;
    this.tokenTranslator = tokenTranslator;
  }

  public VerifiedUser verifyToken(final String token) {
    final DecodedJWT decodedJWT = jwtVerifier.verify(token);
    return tokenTranslator.decode(decodedJWT);
  }
}
