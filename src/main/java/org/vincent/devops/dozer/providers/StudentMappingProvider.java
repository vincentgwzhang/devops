package org.vincent.devops.dozer.providers;

import org.dozer.loader.api.BeanMappingBuilder;
import static org.dozer.loader.api.TypeMappingOptions.wildcard;
import org.springframework.stereotype.Component;
import org.vincent.devops.dto.StudentDTO;
import org.vincent.devops.entity.Student;

import java.util.Arrays;
import java.util.Collection;

@Component
public class StudentMappingProvider implements MappingProvider {
    @Override
    public Collection<BeanMappingBuilder> getMapperConfigurations() {
        return Arrays.asList(transfer());
    }

    private BeanMappingBuilder transfer() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(Student.class, StudentDTO.class, wildcard(false))
                .fields("id","id")
                .fields("name","name")
                .fields("address","address")
                ;
            }
        };
    }

}
