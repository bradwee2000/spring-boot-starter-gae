package com.bwee.springboot.gae.storage;

import com.google.api.client.util.IOUtils;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class StorageService {
  private static final Logger LOG = LoggerFactory.getLogger(StorageService.class);

  private final GcsService gcsService;
  private final GcsFileOptions fileOptions;

  public StorageService(final GcsService gcsService, final GcsFileOptions fileOptions) {
    this.gcsService = gcsService;
    this.fileOptions = fileOptions;
  }

  public void store(final String bucket, final String filename, final InputStream is) {
    try {
      final GcsFilename gcsFilename = new GcsFilename(bucket, filename);

      try(final GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFilename, fileOptions)) {
        IOUtils.copy(is, Channels.newOutputStream(outputChannel));
      }

    } catch (IOException e) {
      throw new StorageException(e);
    }
  }

  public void store(final String bucket, final String filename, final byte[] bytes) {
    try {
      final GcsFilename gcsFilename = new GcsFilename(bucket, filename);

      gcsService.createOrReplace(gcsFilename, fileOptions, ByteBuffer.wrap(bytes));

    } catch (IOException e) {
      throw new StorageException(e);
    }
  }
}
