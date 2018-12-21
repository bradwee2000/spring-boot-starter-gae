package com.bwee.springboot.gae.objectify;

import com.bwee.springboot.gae.model.entity.ConfigEntity;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.impl.translate.TranslatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bradwee2000@gmail.com
 */
public class ObjectifyFactoryFactory implements FactoryBean<ObjectifyFactory>, InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(ObjectifyFactoryFactory.class);

  private final ApplicationContext applicationContext;
  private final MemcacheService memcacheService;
  private final List<Class<?>> classes = Lists.newArrayList();
  private final List<String> basePackageList;
  private ObjectifyFactory objectifyFactory;

  public ObjectifyFactoryFactory(ApplicationContext applicationContext,
                                 MemcacheService memcacheService,
                                 final String basePackages) {
    this.applicationContext = applicationContext;
    this.memcacheService = memcacheService;
    this.basePackageList = Splitter.on(',')
        .trimResults()
        .omitEmptyStrings()
        .splitToList(basePackages + "," + ConfigEntity.class.getPackage().getName());
  }

  @Override
  public void afterPropertiesSet() {
    LOG.info("Initializing Objectify.");

    objectifyFactory = new ObjectifyFactory(new MemcacheServiceAdapter(memcacheService));

    if (!basePackageList.isEmpty()) {
      classes.addAll(doScan());
    }

    for (final Class<?> clazz : classes) {
      this.objectifyFactory.register(clazz);
      LOG.info(" - Registered entity: {}", clazz.getName());
    }

    scanTranslatorFactories();

    ObjectifyService.init(objectifyFactory);
  }

  private List<Class<?>> doScan() {
    final List<Class<?>> classes = new ArrayList<>();

    for (final String basePackage : basePackageList) {
      LOG.info("Scanning package [" + basePackage + "]");
      final ClassPathScanningCandidateComponentProvider scanner =
          new ClassPathScanningCandidateComponentProvider(false);
      scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
      final Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
      for (final BeanDefinition candidate : candidates) {
        Class<?> clazz = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
        classes.add(clazz);
      }
    }
    return classes;
  }

  private void scanTranslatorFactories() {
    final Map<String, TranslatorFactory> beans =
        applicationContext.getBeansOfType(TranslatorFactory.class);

    for (final Map.Entry<String, TranslatorFactory> e : beans.entrySet()) {
      objectifyFactory.getTranslators().add(e.getValue());
      LOG.info(" - Added translator: {}", e.getKey());
    }
  }

  @Override
  public ObjectifyFactory getObject() throws Exception {
    return objectifyFactory;
  }

  @Override
  public Class<?> getObjectType() {
    return ObjectifyFactory.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

}
