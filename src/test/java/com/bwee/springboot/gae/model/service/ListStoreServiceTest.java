package com.bwee.springboot.gae.model.service;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author bradwee2000@gmail.com
 */
public class ListStoreServiceTest {

  private ListStoreService<String> listStoreService;
  private ConfigService configService;
  private ConfigService.PropertyConverter propertyConverter;
  private List<String> list;

  @Before
  public void before() {
    list = Lists.newArrayList();

    configService = mock(ConfigService.class);
    propertyConverter = mock(ConfigService.PropertyConverter.class);

    when(configService.getProperty("test.list")).thenReturn(propertyConverter);
    when(propertyConverter.asType(any())).thenReturn(Optional.of(list));

    listStoreService = new ListStoreService<>("test.list", configService);
  }

  @Test
  public void testGetWithNoValues_shouldReturnEmptyList() {
    final List<String> list = listStoreService.get();
    assertThat(list).isEmpty();
  }

  @Test
  public void testGetExistingValues_shouldReturnListWithValues() {
    list.add("A");
    final List<String> list = listStoreService.get();
    assertThat(list).containsExactly("A");
  }

  @Test
  public void testPut_shouldAddNewValueAndSave() {
    list.add("A");
    list.add("B");

    final List<String> newList = listStoreService.add("C");

    assertThat(newList).containsExactly("A", "B", "C");

    verify(configService).setProperty("test.list", newList);
  }

  @Test
  public void testPutAll_shouldAddAllNewValueAndSave() {
    list.add("A");
    list.add("B");

    final List<String> newList = listStoreService.addAll(Lists.newArrayList("C", "D"));

    assertThat(newList).containsExactly("A", "B", "C", "D");

    verify(configService).setProperty("test.list", newList);
  }

  @Test
  public void testRemove_shouldRemoveValueAndSave() {
    list.add("A");
    list.add("B");

    final List<String> newList = listStoreService.remove("A");

    assertThat(newList).containsExactly("B");

    verify(configService).setProperty("test.list", newList);
  }

  @Test
  public void testRemoveAll_shouldRemoveValuesAndSave() {
    list.add("A");
    list.add("B");
    list.add("C");

    final List<String> newList = listStoreService.removeAll(Lists.newArrayList("A", "C"));

    assertThat(newList).containsExactly("B");

    verify(configService).setProperty("test.list", newList);
  }
}