package com.bwee.springboot.gae.cache.serializer;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author bradwee2000@gmail.com
 */
@Component
public class ResponseEntityCacheSerializer implements CacheSerializer<byte[], ResponseEntity> {

  private final Gson gson;

  @Autowired
  public ResponseEntityCacheSerializer(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public byte[] serialize(final ResponseEntity response) {
    return gson.toJson(new Model(response)).getBytes();
  }

  @Override
  public ResponseEntity deserialize(final byte[] bytes) {
    return gson.fromJson(new String(bytes), Model.class).toResponseEntity();
  }

  @Override
  public Class<ResponseEntity> getType() {
    return ResponseEntity.class;
  }

  /**
   * Wrapper
   */
  public static class Model {
    private HttpStatus status;
    private HttpHeaders headers;
    private Object body;

    public Model() {}

    public Model(final ResponseEntity response) {
      this.status = response.getStatusCode();
      this.headers = response.getHeaders();
      this.body = response.getBody();
    }

    public ResponseEntity toResponseEntity() {
      return ResponseEntity.status(status).headers(headers).body(body);
    }
  }
}
