package org.vincent.devops.dozer;

import org.dozer.Mapper;

import java.util.ArrayList;
import java.util.List;

public class DozerMapper {

    private final Mapper mapper;

    public DozerMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public <T> List<T> map(List<?> source, Class<T> destinationClass) {
        final List<T> target = new ArrayList<>();

        for (final Object element : source)
            target.add(mapper.map(element, destinationClass));

        return target;
    }

    public <T> List<T> map(List<?> source, Class<T> destinationClass, String mapId) {
        final List<T> target = new ArrayList<>();

        for (final Object element : source)
            target.add(mapper.map(element, destinationClass, mapId));

        return target;
    }

    public <T> T map(Object source, Class<T> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    public <T> T map(Object source, Class<T> destinationClass, String mapId) {
        return mapper.map(source, destinationClass, mapId);
    }

    public void map(Object source, Object destination) {
        mapper.map(source, destination);
    }

    public void map(Object source, Object destination, String mapId) {
        mapper.map(source, destination, mapId);
    }

}