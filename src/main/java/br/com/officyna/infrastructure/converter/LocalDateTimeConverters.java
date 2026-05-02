package br.com.officyna.infrastructure.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeConverters {

    // Converte de LocalDateTime para Date (o que o MongoDB entende)
    @WritingConverter
    public enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        INSTANCE;
        @Override
        public Date convert(LocalDateTime source) {
            // Converte o tempo local para o instante exato em UTC
            return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    // Converte de Date (do MongoDB) para LocalDateTime
    @ReadingConverter
    public enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        INSTANCE;
        @Override
        public LocalDateTime convert(Date source) {
            // Converte de volta para o fuso horário local do sistema
            return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}
