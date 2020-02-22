package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.exception.AuthorizationException;
import com.bwee.springboot.gae.auth.user.AuthUser;

/**
 * @author bradwee2000@gmail.com
 */
public class AuthTokenVerifier<T extends AuthUser> {

  private final JWTVerifier jwtVerifier;
  private final TokenTranslator<T> tokenTranslator;

  public AuthTokenVerifier(final JWTVerifier jwtVerifier,
                           final TokenTranslator<T> tokenTranslator) {
    this.jwtVerifier = jwtVerifier;
    this.tokenTranslator = tokenTranslator;
  }

  public T verifyToken(final String token) {
    try {
      final DecodedJWT decodedJWT = jwtVerifier.verify(token);
      return tokenTranslator.decode(decodedJWT);

    } catch (final JWTDecodeException e) {
      throw AuthorizationException.invalidToken(token);
    } catch (final TokenExpiredException e) {
      throw AuthorizationException.expiredToken(token);
    } catch (final SignatureVerificationException e) {
      throw AuthorizationException.invalidSignature(token);
    }
  }
}
