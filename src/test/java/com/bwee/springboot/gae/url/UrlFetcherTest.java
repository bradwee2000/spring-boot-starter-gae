package com.bwee.springboot.gae.url;

import com.google.appengine.api.urlfetch.URLFetchService;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class UrlFetcherTest {
    private UrlFetcher urlFetcher;
    private URLFetchService urlFetchService;

    @Before
    public void before() {
        urlFetchService = mock(URLFetchService.class);
        urlFetcher = new UrlFetcher(urlFetchService);
    }

    @Test
    public void testConnect_shouldReturnFetchBuilder() {
        final String url = "http://localhost:8080/test/url";
        final UrlFetchBuilder urlFetchBuilder = urlFetcher.connect(url);
        assertThat(urlFetchBuilder.getUrl()).isEqualTo(url);
    }
}