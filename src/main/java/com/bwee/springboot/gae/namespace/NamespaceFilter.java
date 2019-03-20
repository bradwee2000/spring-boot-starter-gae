package com.bwee.springboot.gae.namespace;

import com.google.appengine.api.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author bradwee2000@gmail.com
 */
public class NamespaceFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(NamespaceFilter.class);

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    LOG.info("Namespace={}", NamespaceManager.get());
  }
}
