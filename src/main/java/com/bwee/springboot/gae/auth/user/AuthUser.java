package com.bwee.springboot.gae.auth.user;

import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public interface AuthUser {

  String getId();

  String getName();

  List<String> getRoles();
}
