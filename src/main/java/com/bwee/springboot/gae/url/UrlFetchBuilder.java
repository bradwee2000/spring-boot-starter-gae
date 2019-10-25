package com.bwee.springboot.gae.url;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.collect.Maps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Future;

public class UrlFetchBuilder {
    private final Map<String, String> params = Maps.newHashMap();
    private final URLFetchService urlFetchService;
    private final String url;

    public UrlFetchBuilder(final URLFetchService urlFetchService, final String url) {
        this.urlFetchService = urlFetchService;
        this.url = url;
    }

    public UrlFetchBuilder data(final String key, final String value) {
        params.put(key, value);
        return this;
    }

    public Future<HTTPResponse> get() {
        return request(HTTPMethod.GET);
    }

    public Future<HTTPResponse> post() {
        return request(HTTPMethod.POST);
    }

    protected String getUrl() {
        return url;
    }

    protected Map<String, String> getParams() {
        return Maps.newHashMap(params);
    }

    private Future<HTTPResponse> request(final HTTPMethod method) {
        try {
            final HTTPRequest request = new HTTPRequest(new URL(url), method);
            request.setPayload(getParamsAsBytes());
            return urlFetchService.fetchAsync(request);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert parameter to byte data.
     * @return byte data
     */
    protected byte[] getParamsAsBytes() {
        final StringBuilder payloadBuilder = new StringBuilder();
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            payloadBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return payloadBuilder.toString().getBytes();
    }
}
