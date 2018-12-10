package com.bwee.springboot.gae.model.translator;

import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.ValueType;
import com.google.gson.Gson;
import com.googlecode.objectify.impl.translate.SimpleTranslatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bradwee2000@gmail.com
 */
public class JsonTranslatorFactory<T> extends SimpleTranslatorFactory<T, String> {
  private static final Logger LOG = LoggerFactory.getLogger(JsonTranslatorFactory.class);
  private static final Gson gson = new Gson();
  private final Class<T> clazz;

  public JsonTranslatorFactory(final Class<T> clazz) {
    super(clazz, ValueType.STRING, ValueType.NULL);
    this.clazz = clazz;
  }

  @Override
  protected T toPojo(final Value<String> value) {
    return gson.fromJson(value.get(), clazz);
  }

  @Override
  protected Value<String> toDatastore(final T t) {
    return StringValue.of(gson.toJson(t));
  }
}
