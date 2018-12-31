package org.vincent.devops.dozer.convertor;

import org.dozer.DozerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vincent.devops.system.SystemConstant.LONG_DATE_FORMAT;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStringConvertor extends DozerConverter<Date, String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public DateStringConvertor() {
        super(Date.class, String.class);
    }

    @Override
    public String convertTo(Date source, String destination) {
        DateFormat dateFormat = new SimpleDateFormat(LONG_DATE_FORMAT);
        return dateFormat.format(source);
    }

    @Override
    public Date convertFrom(String source, Date destination) {
        DateFormat dateFormat = new SimpleDateFormat(LONG_DATE_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(source);
        } catch (ParseException e) {
            logger.error("Error happen when parsing source {}", source);
        }
        return date;
    }

}