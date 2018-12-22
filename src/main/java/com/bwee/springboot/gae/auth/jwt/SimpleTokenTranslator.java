package com.bwee.springboot.gae.auth.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bwee.springboot.gae.auth.user.VerifiedUser;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public class SimpleTokenTranslator extends TokenTranslator<VerifiedUser> {
  private static final String FIRSTNAME = "fn";
  private static final String LASTNAME = "ln";
  private static final String ROLES = "rl";

  private final Clock clock;

  public SimpleTokenTranslator(final Clock clock) {
    this.clock = clock;
  }

  @Override
  public JWTCreator.Builder toJwt(final VerifiedUser user, final JWTCreator.Builder jwtBuilder) {
    final String jwtId = user.getId() + "-" + LocalDateTime.now(clock).format(DateTimeFormatter.ISO_DATE_TIME);

    return jwtBuilder.withJWTId(jwtId)
        .withSubject(user.getId())
        .withClaim(FIRSTNAME, user.getFirstname())
        .withClaim(LASTNAME, user.getLastname())
        .withArrayClaim(ROLES, user.getRoles().toArray(new String[]{}));
  }

  @Override
  public VerifiedUser decode(DecodedJWT decodedJWT) {
    final String id = decodedJWT.getSubject();
    final String firstname = decodedJWT.getClaim(FIRSTNAME).asString();
    final String lastname = decodedJWT.getClaim(LASTNAME).asString();
    final List<String> roles = decodedJWT.getClaim(ROLES).asList(String.class);

    final VerifiedUser user = new VerifiedUser(id, firstname, lastname, roles);
    return user;
  }
}
