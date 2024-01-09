package ru.job4j.site.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.handler.RestTemplateResponseErrorHandler;

/**
 * CheckDev пробное собеседование
 * AppConfig создаём бин RestTemplate и добавляем в него RestTemplateResponseErrorHandler.
 *
 * @author Oleg Ershov
 * @version 07.01.2024 19:18
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }
}
