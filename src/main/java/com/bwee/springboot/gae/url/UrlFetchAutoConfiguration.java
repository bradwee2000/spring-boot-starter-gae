package com.bwee.springboot.gae.url;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlFetchAutoConfiguration {

    @Bean
    public UrlFetcher urlFetcher(final URLFetchService urlFetchService) {
        return new UrlFetcher(urlFetchService);
    }

    @Bean
    @ConditionalOnMissingBean(URLFetchService.class)
    public URLFetchService urlFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }
}
