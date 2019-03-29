package com.bwee.springboot.gae.model.dao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.translate.TranslatorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author bradwee2000@gmail.com
 */
public abstract class DatastoreTest {

  private static final String NAMESPACE = "test-app";
  private static final double DATASTORE_CONSISTENCY = 1.0;
  private static final LocalDatastoreHelper helper = LocalDatastoreHelper.create(DATASTORE_CONSISTENCY);

  private Closeable session;

  public static void registerEntityClasses(Class clazz, Class ... more) {
    Lists.asList(clazz, more).forEach(c -> ObjectifyService.register(c));
  }

  public static void registerTranslatorFactories(TranslatorFactory translatorFactory,
                                                 TranslatorFactory ... more) {
    Lists.asList(translatorFactory, more)
        .forEach(translator -> ObjectifyService.factory().getTranslators().add(translator));
  }

  @BeforeClass
  public static void startDatastore() throws IOException, InterruptedException {
    helper.start();
    final Datastore datastore = helper.getOptions().toBuilder().setNamespace(NAMESPACE).build().getService();

    ObjectifyService.init(new ObjectifyFactory(datastore));
  }

  @AfterClass
  public static void teardownDatastore() throws InterruptedException, TimeoutException, IOException {
    helper.stop();
  }

  @Before
  public void beginOfy() {
    this.session = ObjectifyService.begin();
  }

  @After
  public void resetDatastore() throws IOException {
//      AsyncCacheFilter.complete();
      session.close();
      helper.reset();
  }
}
