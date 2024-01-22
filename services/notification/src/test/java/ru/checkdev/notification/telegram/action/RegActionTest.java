package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.service.ChatIdService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Testing RegAction class
 *
 * @author Oleg Ershov
 * @since 22.01.2024
 */

@ExtendWith(MockitoExtension.class)
public class RegActionTest {

    @Mock
    private TgAuthCallWebClint authCallWebClint;
    @Mock
    private ChatIdService chatIdService;
    @Mock
    TgConfig tgConfig;
    @InjectMocks
    private RegAction regAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountAlreadyExists() {
        var response = "Данный аккаунт Telegram уже зарегистрирован на сайте";
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.of(new ChatId()));

        assertThat(regAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenAccountNotExists() {
        var response = "Введите ваше имя и email для регистрации в формате \"имя#email\":";
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.empty());

        assertThat(regAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenException() {
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("username#email@mail.ru");
        when(tgConfig.checkFormat(any())).thenReturn(Map.of(
                "username", "username",
                "email", "email@mail.ru"
                ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        when(tgConfig.getPassword()).thenReturn("password");
        doThrow(new RuntimeException()).when(authCallWebClint).doPost(any(), any());

        var response = "Сервис не доступен попробуйте позже" + System.lineSeparator()
                + "/start";
        assertThat(regAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenError() {
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(message.getText()).thenReturn("username#email@mail.ru");
        when(tgConfig.checkFormat(any())).thenReturn(Map.of(
                "username", "username",
                "email", "email@mail.ru"
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        when(tgConfig.getPassword()).thenReturn("password");
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(new Object()));

        var errorMessage = "error message";
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("error", errorMessage));
        var response = "Ошибка регистрации: " + errorMessage;
       assertThat(regAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenRegSuccess() {
        var username = "username";
        var email = "email@mail.ru";
        var password = "password";
        var sl = System.lineSeparator();

        var message = new Message();
        message.setText(username + "#" + email);
        var chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);

        when(tgConfig.checkFormat(any())).thenReturn(Map.of(
                "username", username,
                "email", email
        ));
        when(tgConfig.isEmail(any())).thenReturn(true);
        when(tgConfig.getPassword()).thenReturn(password);
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(new Object()));
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("username", "username"));

        var response = "Вы зарегистрированы: " + sl
                + "Логин: " + email + sl
                + "Пароль: " + password + sl
                + null;
        assertThat(regAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenInvalidData() {
        var sl = System.lineSeparator();

        var message = new Message();
        message.setText("username/email@mail.ru");
        var chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);

        when(tgConfig.checkFormat(any())).thenReturn(new HashMap<>());

        var response = "Некорректный формат данных." + sl
                + "Попробуйте снова" + sl
                + "/new";
        assertThat(regAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

}