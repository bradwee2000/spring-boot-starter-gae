package com.bwee.springboot.gae.model.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class MapStoreServiceTest {

  private MapStoreService<String, String> mapStoreService;
  private ConfigService configService;
  private ConfigService.PropertyConverter propertyConverter;
  private Map<String, String> map;

  @Before
  public void before() {
    map = new HashMap<>();

    configService = mock(ConfigService.class);
    propertyConverter = mock(ConfigService.PropertyConverter.class);

    when(configService.getProperty("test.map")).thenReturn(propertyConverter);
    when(propertyConverter.asType(any())).thenReturn(Optional.of(map));

    mapStoreService = new MapStoreService("test.map", configService);
  }

  @Test
  public void testGetWithNoValues_shouldReturnEmptyMap() {
    final Map<String, String> map = mapStoreService.get();
    assertThat(map).isEmpty();
  }

  @Test
  public void testGetExistingValues_shouldReturnMapWithValues() {
    map.put("A", "BCD");
    final Map<String, String> map = mapStoreService.get();
    assertThat(map).containsOnlyKeys("A").containsValue("BCD");
  }

  @Test
  public void testPut_shouldAddNewValueAndSave() {
    map.put("A", "Mary");
    map.put("B", "Susan");

    final Map<String, String> newMap = mapStoreService.put("A", "John");

    assertThat(newMap.get("A")).isEqualTo("John");
    assertThat(newMap.get("B")).isEqualTo("Susan");
    assertThat(newMap).containsOnlyKeys("A", "B");

    verify(configService).setProperty("test.map", newMap);
  }

  @Test
  public void testPutAll_shouldAddAllNewValueAndSave() {
    map.put("A", "Mary");
    map.put("B", "Susan");

    final Map<String, String> newMap = mapStoreService.putAll(ImmutableMap.of("A", "John", "C", "Jason"));

    assertThat(newMap.get("A")).isEqualTo("John");
    assertThat(newMap.get("B")).isEqualTo("Susan");
    assertThat(newMap.get("C")).isEqualTo("Jason");
    assertThat(newMap).containsOnlyKeys("A", "B", "C");

    verify(configService).setProperty("test.map", newMap);
  }

  @Test
  public void testRemove_shouldRemoveValueAndSave() {
    map.put("A", "Mary");
    map.put("B", "Susan");

    final Map<String, String> newMap = mapStoreService.remove("A");

    assertThat(newMap.get("B")).isEqualTo("Susan");
    assertThat(newMap).containsOnlyKeys("B");

    verify(configService).setProperty("test.map", newMap);
  }

  @Test
  public void testRemoveAll_shouldRemoveValuesAndSave() {
    map.put("A", "Mary");
    map.put("B", "Susan");
    map.put("C", "Charlie");

    final Map<String, String> newMap = mapStoreService.removeAll(Lists.newArrayList("A", "C"));

    assertThat(newMap.get("B")).isEqualTo("Susan");
    assertThat(newMap).containsOnlyKeys("B");

    verify(configService).setProperty("test.map", newMap);
  }
}