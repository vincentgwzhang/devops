package org.vincent.devops.dozer.providers;

import org.dozer.loader.api.BeanMappingBuilder;

import java.util.Collection;

public interface MappingProvider {
    Collection<BeanMappingBuilder> getMapperConfigurations();
}
