package ru.job4j.site.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.job4j.site.exception.IdNotFoundException;
import java.io.IOException;

/**
 * CheckDev пробное собеседование
 * RestTemplateResponseErrorHandler реализует методы интерфейса ResponseErrorHandler для проверки ошибок и генерации
 * исключений в случае их наличия.
 *
 * @author Oleg Ershov
 * @version 07.01.2024 19:21
 */
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new IdNotFoundException("ID не найден");
        }
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                && httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new IdNotFoundException("Пользователь не найден");
        }
    }
}
