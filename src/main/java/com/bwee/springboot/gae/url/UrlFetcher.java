package com.bwee.springboot.gae.url;

import com.google.appengine.api.urlfetch.URLFetchService;

public class UrlFetcher {

    private final URLFetchService urlFetchService;

    public UrlFetcher(final URLFetchService urlFetchService) {
        this.urlFetchService = urlFetchService;
    }

    public UrlFetchBuilder connect(final String url) {
        return new UrlFetchBuilder(urlFetchService, url);
    }
}
