package com.bwee.springboot.gae.url;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UrlFetchBuilderTest {
    private URLFetchService urlFetchService;
    private Future<HTTPResponse> futureResponse;

    private UrlFetchBuilder urlFetchBuilder;

    @Before
    public void before() {
        urlFetchService = mock(URLFetchService.class);
        futureResponse = mock(Future.class);

        when(urlFetchService.fetchAsync(any(HTTPRequest.class))).thenReturn(futureResponse);

        urlFetchBuilder = new UrlFetchBuilder(urlFetchService, "http://localhost:8080");
    }

    @Test
    public void testGetParamsAsBytes_shouldConvertParamsToBytes() {
        assertThat(new String(urlFetchBuilder
                .data("data1", "value1")
                .data("data2", "value2").getParamsAsBytes()))
                .contains("&data1=value1")
                .contains("&data2=value2");
    }

    @Test
    public void testAddData_shouldAddDataToRequest() {
        final Map<String, String> params = urlFetchBuilder.data("data1", "value1").data("data2", "value2").getParams();

        assertThat(params.get("data1")).isEqualTo("value1");
        assertThat(params.get("data2")).isEqualTo("value2");
    }

    @Test
    public void testPost_shouldRequestPost() {
        final ArgumentCaptor<HTTPRequest> requestCaptor = ArgumentCaptor.forClass(HTTPRequest.class);
        final Future<HTTPResponse> response = urlFetchBuilder.data("data", "value").post();

        assertThat(response).isEqualTo(futureResponse);

        verify(urlFetchService).fetchAsync(requestCaptor.capture());

        final HTTPRequest request = requestCaptor.getValue();
        assertThat(request.getMethod()).isEqualTo(HTTPMethod.POST);
        assertThat(request.getPayload()).isEqualTo("&data=value".getBytes());
        assertThat(request.getURL().toString()).isEqualTo("http://localhost:8080");
    }

    @Test
    public void testGet_shouldRequestGet() {
        final ArgumentCaptor<HTTPRequest> requestCaptor = ArgumentCaptor.forClass(HTTPRequest.class);
        final Future<HTTPResponse> response = urlFetchBuilder.data("data", "value").get();

        assertThat(response).isEqualTo(futureResponse);

        verify(urlFetchService).fetchAsync(requestCaptor.capture());

        final HTTPRequest request = requestCaptor.getValue();
        assertThat(request.getMethod()).isEqualTo(HTTPMethod.GET);
        assertThat(request.getPayload()).isEqualTo("&data=value".getBytes());
        assertThat(request.getURL().toString()).isEqualTo("http://localhost:8080");
    }

    @Test
    public void testGetWithHeaders_shouldAddHeaders() {
        final ArgumentCaptor<HTTPRequest> requestCaptor = ArgumentCaptor.forClass(HTTPRequest.class);

        urlFetchBuilder.header("data", "value")
                .header("data2", "value2")
                .header("data", "value3")
                .get();

        verify(urlFetchService).fetchAsync(requestCaptor.capture());

        final HTTPRequest request = requestCaptor.getValue();
        assertThat(request.getMethod()).isEqualTo(HTTPMethod.GET);
        assertThat(request.getHeaders()).extracting(h -> h.getName() + "=" + h.getValue())
                .containsExactlyInAnyOrder("data=value3", "data2=value2");
    }
}