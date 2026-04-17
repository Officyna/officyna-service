package br.com.officyna.infrastructure.config;

import br.com.officyna.infrastructure.converter.LocalDateTimeConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                LocalDateTimeConverters.LocalDateTimeToDateConverter.INSTANCE,
                LocalDateTimeConverters.DateToLocalDateTimeConverter.INSTANCE
        ));
    }
}