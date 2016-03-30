package es.unizar.iaaa.geofencing.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by javier on 30/03/16.
 */
@Component
public class GeometryConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.modules(new JtsModule());
        return b;
    }

}
