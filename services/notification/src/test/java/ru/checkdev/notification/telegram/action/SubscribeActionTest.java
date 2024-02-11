package ru.checkdev.notification.telegram.action;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testing SubscribeAction class
 *
 * @author Oleg Ershov
 * @since 30.01.2024
 */
@ExtendWith(MockitoExtension.class)
class SubscribeActionTest {

    @Mock
    TgAuthCallWebClint tgAuthCallWebClint;
    @Mock
    TgConfig tgConfig;
    @Mock
    private ChatIdService chatIdService;
    @InjectMocks
    private SubscribeAction subscribeAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountNotExists() {
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.isReg(any())).thenReturn(false);

        var response = "Данный аккаунт Telegram не зарегистрирован";
        assertThat(subscribeAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenAccountExists() {
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.isReg(any())).thenReturn(true);

        var response =  "Введите ваш email и пароль для подписки на уведомления в формате \"email#password\":";
        assertThat(subscribeAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenInvalidData() {
        var sl = System.lineSeparator();
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("email@mail.ru/password");
        when(tgConfig.parseEmailAndPassword(any())).thenReturn(Map.of());

        var response = "Некорректный формат данных." + sl
                + "Попробуйте снова" + sl
                + "/subscribe";
        assertThat(subscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenInvalidEmail() {
        var sl = System.lineSeparator();
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("mail.ru#password");
        when(tgConfig.parseEmailAndPassword(any())).thenReturn(Map.of(
                "email", "mail.ru", "password", "password"
        ));
        when(tgConfig.isEmail(any())).thenReturn(false);

        var response =  "Email: mail.ru некорректный." + sl
                + "Попробуйте снова." + sl
                + "/subscribe";
        assertThat(subscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenException() {
        var sl = System.lineSeparator();
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("email@mail.ru#password");
        when(tgConfig.parseEmailAndPassword(any())).thenReturn(Map.of(
                "email", "email@mail.ru", "password", "password"
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        doThrow(new RuntimeException()).when(tgAuthCallWebClint).doGet(any());

        var response = "Сервис не доступен попробуйте позже" + sl
                + "/start";
        assertThat(subscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenError() {
        var message = mock(Message.class);
        var errorMessage = "error message";

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("email@mail.ru#password");
        when(tgConfig.parseEmailAndPassword(any())).thenReturn(Map.of(
                "email", "email@mail.ru", "password", "password"
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.just(new PersonDTO()));

        var response = "Ошибка оформления подписки: email или пароль введены неверно";
        assertThat(subscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenSubscribeSuccess() {
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("email@mail.ru#password");
        when(tgConfig.parseEmailAndPassword(any())).thenReturn(Map.of(
                "email", "email@mail.ru", "password", "password"
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        PersonDTO personDTO = new PersonDTO();
        personDTO.setEmail("email@mail.ru");
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.just(personDTO));
        when(chatIdService.findByChatId(any())).thenReturn(Optional.of(new ChatId()));

        var response = "Вы подписаны на уведомления";
        assertThat(subscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

}