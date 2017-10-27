package de.viarepair.abrechnungssystem.ui.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToLongConverter;

@SuppressWarnings("serial")
public class CurrencyConverter extends StringToLongConverter {

    @Override
    protected NumberFormat getFormat(Locale locale) {
        DecimalFormat df = (DecimalFormat) NumberFormat
                .getNumberInstance(locale == null ? Locale.getDefault() : locale);
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df;
    }

    @Override
    public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale)
            throws ConversionException {
        Number n = super.convertToNumber(value, targetType, locale);
        return n == null ? null : Double.valueOf(100 * n.doubleValue()).longValue();
    }

    @Override
    public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        return value == null ? null : getFormat(locale).format(Double.valueOf(value) / 100);
    }
}