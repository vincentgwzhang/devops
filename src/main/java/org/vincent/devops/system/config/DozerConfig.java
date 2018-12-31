package org.vincent.devops.system.config;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.vincent.devops.dozer.DozerMapper;
import org.vincent.devops.dozer.providers.MappingProvider;

import java.util.Set;
import java.util.function.Consumer;

@Configuration
@ComponentScan(DozerConfig.DOZER_PACKAGE)
public class DozerConfig {

    public static final String DOZER_PACKAGE = "org.vincent.devops.dozer";

    @Bean
    public DozerMapper dozerMapper(Set<MappingProvider> mapperConfigurations) {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapperConfigurations.forEach(configureMapper(mapper));

        return new DozerMapper(mapper);
    }

    private Consumer<MappingProvider> configureMapper(DozerBeanMapper mapper) {
        return mapperConfigurationProvider -> mapperConfigurationProvider.getMapperConfigurations()
                .forEach(mapper::addMapping);
    }

}
