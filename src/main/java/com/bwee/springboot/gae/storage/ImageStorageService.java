package com.bwee.springboot.gae.storage;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bradwee2000@gmail.com
 */
public class ImageStorageService extends StorageService {
  private static final Logger LOG = LoggerFactory.getLogger(ImageStorageService.class);

  public ImageStorageService(final GcsService gcsService) {
    super(gcsService, new GcsFileOptions.Builder().mimeType("image/jpeg").build());
  }
}
