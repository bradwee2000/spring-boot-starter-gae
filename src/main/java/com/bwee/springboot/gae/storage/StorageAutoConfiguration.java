package com.bwee.springboot.gae.storage;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class StorageAutoConfiguration {

  @Bean
  public GcsService gcsService() {
    return GcsServiceFactory.createGcsService();
  }

  @Bean
  public ImageStorageService imageStorageService(final GcsService gcsService) {
    return new ImageStorageService(gcsService);
  }
}
