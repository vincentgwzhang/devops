package org.vincent.devops.dozer.convertor;

import org.dozer.DozerConverter;

public class EnumConvertor extends DozerConverter<Enum, String> {

    public EnumConvertor() {
        super(Enum.class, String.class);
    }

    @Override
    public String convertTo(Enum source, String destination) {
        return source.name();
    }

    @Override
    public Enum convertFrom(String source, Enum destination) {
        return Enum.valueOf(Enum.class, source);
    }
}
