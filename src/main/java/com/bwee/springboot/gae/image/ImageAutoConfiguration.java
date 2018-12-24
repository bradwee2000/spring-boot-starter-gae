package com.bwee.springboot.gae.image;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bradwee2000@gmail.com
 */
@Configuration
public class ImageAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(ImageService.class)
  public ImageService imageService(final OutputSettings outputSettings) {
    return new ImageService(ImagesServiceFactory.getImagesService(), outputSettings);
  }

  @Bean
  @ConditionalOnMissingBean(OutputSettings.class)
  public OutputSettings jpegOutputSettings(@Value("${image.output.jpeg.quality:95}") final int jpegQuality) {
    final OutputSettings outputSettings = new OutputSettings(ImagesService.OutputEncoding.JPEG);
    outputSettings.setQuality(jpegQuality);
    return outputSettings;
  }
}
