package com.bwee.springboot.gae.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenSigner {

  private final Algorithm signingAlgorithm;
  private final TokenTranslator tokenTranslator;

  public AuthTokenSigner(final Algorithm signingAlgorithm,
                         final TokenTranslator tokenTranslator) {
    this.signingAlgorithm = signingAlgorithm;
    this.tokenTranslator = tokenTranslator;
  }

  public String signToken(final VerifiedUser user) {
    return tokenTranslator.toJwt(user, JWT.create()).sign(signingAlgorithm);
  }
}
