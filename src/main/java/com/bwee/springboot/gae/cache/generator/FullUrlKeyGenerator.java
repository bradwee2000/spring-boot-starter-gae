package com.bwee.springboot.gae.cache.generator;

import com.bwee.springboot.gae.cache.CacheContent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bradwee2000@gmail.com
 */
public class FullUrlKeyGenerator implements KeyGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(FullUrlKeyGenerator.class);

  @Override
  public String generateKey(JoinPoint joinPoint, CacheContent cacheContent) {
    final HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    checkNotNull(request, "Can only be used in an Http request context.");

    final String prefix = StringUtils.isEmpty(cacheContent.keyPrefix()) ? "" : cacheContent.keyPrefix() + ".";

    final String queryParams = request.getQueryString();

    final String pathKey = prefix + request.getRequestURL() +
        (StringUtils.isEmpty(queryParams) ? "" : "?" + queryParams);

    return pathKey;
  }

  @Override
  public KeyType getKeyType() {
    return KeyType.FULL_URL;
  }
}
