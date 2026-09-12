package com.niu.core.common.config;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer hutoolJsonNullCustomizer() {
        return builder -> builder.serializerByType(JSONNull.class, new JsonSerializer<JSONNull>() {
            @Override
            public void serialize(JSONNull value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeNull();
            }
        });
    }
}
