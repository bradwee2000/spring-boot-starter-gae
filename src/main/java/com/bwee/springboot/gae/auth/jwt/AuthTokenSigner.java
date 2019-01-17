package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bwee.springboot.gae.auth.user.AuthUser;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenSigner<T extends AuthUser> {

  private final Algorithm signingAlgorithm;
  private final TokenTranslator<T> tokenTranslator;

  public AuthTokenSigner(final Algorithm signingAlgorithm,
                         final TokenTranslator<T> tokenTranslator) {
    this.signingAlgorithm = signingAlgorithm;
    this.tokenTranslator = tokenTranslator;
  }

  public String signToken(final T user) {
    return tokenTranslator.toJwt(user, JWT.create()).sign(signingAlgorithm);
  }
}
