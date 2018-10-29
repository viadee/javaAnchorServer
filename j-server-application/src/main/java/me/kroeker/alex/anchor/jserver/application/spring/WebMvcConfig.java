package me.kroeker.alex.anchor.jserver.application.spring;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(this.createJacksonHttpMessageConverter());
    }

    private MappingJackson2HttpMessageConverter createJacksonHttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

}
