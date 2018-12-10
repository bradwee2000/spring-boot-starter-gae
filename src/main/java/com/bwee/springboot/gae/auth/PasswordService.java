package com.bwee.springboot.gae.auth;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

/**
 * @author bradwee2000@gmail.com
 */
@Component
public class PasswordService {
  private static final Logger LOG = LoggerFactory.getLogger(PasswordService.class);

  // The higher the number of iterations the more
  // expensive computing the hash is for us and
  // also for an attacker.
  private static final int iterations = 20*1000;
  private static final int saltLen = 32;
  private static final int desiredKeyLen = 256;

  /**
   * Computes a salted PBKDF2 hash of given plaintext password
   * suitable for storing in a database. Empty passwords are not supported.
   */
  public String getSaltedHash(String password) {
    byte[] salt = new byte[0];
    try {
      salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
      // store the salt with the password
      return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks whether given plaintext password corresponds to a stored salted hash of the password.
   */
  public boolean check(String password, String stored) {
    String[] saltAndHash = stored.split("\\$");
    if (saltAndHash.length != 2) {
      throw new IllegalStateException("The stored password must have the form 'salt$hash'");
    }

    try {
      String hashOfInput = hash(password, Base64.decodeBase64(saltAndHash[0]));
      return hashOfInput.equals(saltAndHash[1]);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
  // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
  private static String hash(String password, byte[] salt) throws Exception {
    if (password == null || password.length() == 0)
      throw new IllegalArgumentException("Empty passwords are not supported.");
    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    SecretKey key = f.generateSecret(new PBEKeySpec(
        password.toCharArray(), salt, iterations, desiredKeyLen));
    return Base64.encodeBase64String(key.getEncoded());
  }
}
