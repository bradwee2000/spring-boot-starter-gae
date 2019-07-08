package com.bwee.springboot.gae.namespace;

import com.google.appengine.api.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author bradwee2000@gmail.com
 */
public class NamespaceFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(NamespaceFilter.class);

  private final String namespace;

  public NamespaceFilter(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    LOG.debug("Setting namespace to " + namespace);
    NamespaceManager.set(namespace);
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }
}
