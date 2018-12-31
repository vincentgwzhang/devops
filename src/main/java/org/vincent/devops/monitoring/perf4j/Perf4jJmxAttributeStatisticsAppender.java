package org.vincent.devops.monitoring.perf4j;

import lombok.Getter;
import lombok.Setter;
import org.perf4j.aop.Profiled;
import org.perf4j.logback.JmxAttributeStatisticsAppender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@Setter
public class Perf4jJmxAttributeStatisticsAppender extends JmxAttributeStatisticsAppender {

    private String className;

    @Override
    public void start() {
        Class clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return;
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();
        Optional<String> tagNamesOptional = Arrays.stream(declaredMethods).filter(isProfiledMethod()).map(Method::getName).reduce((methodName1, methodName2) -> methodName1.concat(",").concat(methodName2));
        setTagNamesToExpose(tagNamesOptional.orElseThrow(() -> new IllegalArgumentException("No profiled annotated in " + className)));
        super.start();
    }

    private Predicate<Method> isProfiledMethod() {
        return method -> method.isAnnotationPresent(Profiled.class);
    }
}