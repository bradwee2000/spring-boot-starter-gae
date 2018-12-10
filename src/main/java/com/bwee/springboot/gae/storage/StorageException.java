package com.bwee.springboot.gae.storage;

/**
 * @author bradwee2000@gmail.com
 */
public class StorageException extends RuntimeException {

  public StorageException(Exception e) {
    super(e);
  }
}
