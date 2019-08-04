package com.bwee.springboot.gae.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MethodUtils {

    public static <T> List<MethodParameter<T>> extractMethodParameters(final JoinPoint joinPoint,
                                                                       final Class<T> annotatedClass) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final Annotation[][] annotations = method.getParameterAnnotations();

        final List<MethodParameter<T>> parameters = new ArrayList<>();

        // For each parameter
        for (int i=0; i< annotations.length; i++) {

            // For each annotation on parameter
            for (Annotation annotation : annotations[i]) {

                // If annotation is found, add value to attributes
                if (annotatedClass.isInstance(annotation)) {
                    final String paramName = method.getParameters()[i].getName();
                    final String paramValue = String.valueOf(joinPoint.getArgs()[i]);

                    parameters.add(new MethodParameter<T>()
                            .setAnnotation((T) annotation)
                            .setDeclaredName(paramName)
                            .setValue(paramValue));
                    break;
                }
            }
        }
        return parameters;
    }

    public static class MethodParameter<T> {
        private String declaredName;
        private T annotation;
        private Object value;

        public String getDeclaredName() {
            return declaredName;
        }

        public MethodParameter setDeclaredName(String declaredName) {
            this.declaredName = declaredName;
            return this;
        }

        public T getAnnotation() {
            return annotation;
        }

        public MethodParameter setAnnotation(T annotation) {
            this.annotation = annotation;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public MethodParameter setValue(Object value) {
            this.value = value;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodParameter that = (MethodParameter) o;
            return Objects.equals(declaredName, that.declaredName) &&
                    Objects.equals(annotation, that.annotation) &&
                    Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(declaredName, annotation, value);
        }

        @Override
        public String toString() {
            return "MethodParameter{" +
                    "declaredName='" + declaredName + '\'' +
                    ", annotation=" + annotation +
                    ", value=" + value +
                    '}';
        }
    }
}
