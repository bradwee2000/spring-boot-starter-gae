package com.bwee.springboot.gae.task;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author bradwee2000@gmail.com
 */
public class Task {
  private final TaskFactory taskFactory;

  private String name;
  private String queueName;
  private String url;
  private String payload = ""; // error if null
  private Map<String, String> params = Maps.newHashMap();
  private Map<String, String> headers = Maps.newHashMap();
  private TaskMethod method = TaskMethod.GET;

  public Task(final TaskFactory taskService) {
    this.taskFactory = taskService;
  }

  public String getName() {
    return name;
  }

  public Task setName(String name) {
    this.name = name;
    return this;
  }

  public String getQueueName() {
    return queueName;
  }

  public Task setQueueName(String queueName) {
    this.queueName = queueName;
    return this;
  }

  public TaskMethod getMethod() {
    return method;
  }

  public Task setMethod(TaskMethod method) {
    this.method = method;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public Task setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getPayload() {
    return payload;
  }

  public Task setPayload(String payload) {
    this.payload = payload;
    return this;
  }

  public Task param(final String key, final String value) {
    params.put(key, value);
    return this;
  }

  public Task params(final Map<String, String> params) {
    params.putAll(params);
    return this;
  }

  public Task header(final String key, final String value) {
    headers.put(key, value);
    return this;
  }

  public Task header(final Map<String, String> params) {
    headers.putAll(params);
    return this;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void submit() {
    taskFactory.submit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
    return Objects.equal(taskFactory, task.taskFactory) &&
            Objects.equal(name, task.name) &&
            Objects.equal(queueName, task.queueName) &&
            Objects.equal(url, task.url) &&
            Objects.equal(payload, task.payload) &&
            Objects.equal(params, task.params) &&
            method == task.method;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(taskFactory, name, queueName, url, payload, params, method);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("taskFactory", taskFactory)
            .add("name", name)
            .add("queueName", queueName)
            .add("url", url)
            .add("payload", payload)
            .add("params", params)
            .add("method", method)
            .toString();
  }
}
