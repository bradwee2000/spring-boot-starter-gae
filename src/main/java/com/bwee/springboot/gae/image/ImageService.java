package com.bwee.springboot.gae.image;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.images.Transform;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bradwee2000@gmail.com
 */
public class ImageService {
  private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
  private final ImagesService imagesService;
  private final OutputSettings outputSettings;

  public ImageService(final ImagesService imagesService,
                      final OutputSettings outputSettings) {
    this.imagesService = imagesService;
    this.outputSettings = outputSettings;
  }

  public TransformBuilder transform(final byte[] imageBytes) {
    return new TransformBuilder(this, imageBytes);
  }

  public byte[] apply(final TransformBuilder tb) {
    if (tb.transforms.isEmpty()) {
      return tb.getImageBytes();
    }

    final Image image = ImagesServiceFactory.makeImage(tb.imageBytes);
    final Transform transform = ImagesServiceFactory.makeCompositeTransform(tb.transforms);
    final Image transformed = imagesService.applyTransform(transform, image, outputSettings);

    return transformed.getImageData();
  }

  public String dynamicUrl(final String bucketName) {
    ServingUrlOptions options = ServingUrlOptions.Builder
            .withGoogleStorageFileName("/gs/" + bucketName + "/image.jpeg")
            .imageSize(150)
            .crop(true)
            .secureUrl(true);
    String url = imagesService.getServingUrl(options);
    return url;
  }

  /**
   * Transform Builder
   */
  public static class TransformBuilder {
    private final ImageService imageService;
    private final byte[] imageBytes;
    private final List<Transform> transforms = Lists.newArrayList();

    public TransformBuilder(final ImageService imageService, final byte[] imageBytes) {
      this.imageService = imageService;
      this.imageBytes = imageBytes;
    }

    public TransformBuilder resize(int width, int height) {
      transforms.add(ImagesServiceFactory.makeResize(width, height));
      return this;
    }

    public TransformBuilder crop(double leftX, double topY, double rightX, double bottomY) {
      transforms.add(ImagesServiceFactory.makeCrop(leftX, topY, rightX, bottomY));
      return this;
    }

    public TransformBuilder flipHorizontal() {
      transforms.add(ImagesServiceFactory.makeHorizontalFlip());
      return this;
    }

    public TransformBuilder flipVertical() {
      transforms.add(ImagesServiceFactory.makeVerticalFlip());
      return this;
    }

    public byte[] getImageBytes() {
      return imageBytes;
    }

    public byte[] apply() {
      return imageService.apply(this);
    }
  }
}
