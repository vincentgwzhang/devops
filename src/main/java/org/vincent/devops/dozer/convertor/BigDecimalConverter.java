package org.vincent.devops.dozer.convertor;

import com.google.common.base.Strings;
import org.dozer.DozerConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;

public class BigDecimalConverter extends DozerConverter<BigDecimal, String> {

    private static final String DECIMAL_PATTERN = "#,##0.0#";

    public BigDecimalConverter() {
        super(BigDecimal.class, String.class);
    }

    @Override
    public String convertTo(BigDecimal source, String destination) {
        if( source == null ){
            return null;
        }
        return source.toString();
    }

    @Override
    public BigDecimal convertFrom(String source, BigDecimal destination) {
        if( Strings.nullToEmpty(source).isEmpty() ){
            return null;
        }
        return parseStringAsBigDecimalOrThrow(source);
    }

    private BigDecimal parseStringAsBigDecimalOrThrow(String source) {

        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        decimalFormat.applyPattern(DECIMAL_PATTERN);
        decimalFormat.setParseBigDecimal(true);

        ParsePosition parsePosition = new ParsePosition(0);
        BigDecimal parsedValue = (BigDecimal) decimalFormat.parse(source, parsePosition);

        if (parsePosition.getIndex() != source.length()) {
            throw new NumberFormatException(String.format("Invalid value=%s for BigDecimal", source));
        }

        return parsedValue;
    }

}