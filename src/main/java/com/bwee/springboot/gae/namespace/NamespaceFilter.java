package com.bwee.springboot.gae.namespace;

import com.google.appengine.api.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author bradwee2000@gmail.com
 */
public class NamespaceFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(NamespaceFilter.class);

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    LOG.info("Namespace={}", NamespaceManager.get());
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }
}
