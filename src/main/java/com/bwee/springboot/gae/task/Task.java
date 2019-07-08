package com.bwee.springboot.gae.task;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Collection;
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
    private Multimap<String, String> params = MultimapBuilder.hashKeys().arrayListValues().build();
    private Multimap<String, String> headers = MultimapBuilder.hashKeys().arrayListValues().build();
    private TaskMethod method = TaskMethod.GET;
    private String contentType = "application/json; charset=UTF-8";

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
        params.entrySet().forEach(e -> param(e.getKey(), e.getValue()));
        return this;
    }

    public Task params(final String key, final Iterable<String> values) {
        this.params.putAll(key, values);
        return this;
    }

    public Task header(final String key, final String value) {
        headers.put(key, value);
        return this;
    }

    public Task headers(final Map<String, String> headers) {
        headers.entrySet().forEach(e -> header(e.getKey(), e.getValue()));
        return this;
    }

    public Task headers(final String key, final Iterable<String> values) {
        this.headers.putAll(key, values);
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Task setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Map<String, Collection<String>> getParams() {
        return params.asMap();
    }

    public Map<String, Collection<String>> getHeaders() {
        return headers.asMap();
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
