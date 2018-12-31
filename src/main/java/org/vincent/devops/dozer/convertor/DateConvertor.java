package org.vincent.devops.dozer.convertor;

import org.dozer.DozerConverter;

import java.sql.Date;


public class DateConvertor extends DozerConverter<Date, java.util.Date> {

    public DateConvertor() {
        super(Date.class, java.util.Date.class);
    }

    @Override
    public java.util.Date convertTo(Date source, java.util.Date destination) {
        return new java.util.Date(source.getTime());
    }

    @Override
    public Date convertFrom(java.util.Date source, Date destination) {
        return new Date(source.getTime());
    }

}