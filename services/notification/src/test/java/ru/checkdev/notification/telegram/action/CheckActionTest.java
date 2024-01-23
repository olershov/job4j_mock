package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.service.ChatIdService;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing CheckAction class
 *
 * @author Oleg Ershov
 * @since 16.01.2024
 */

@ExtendWith(MockitoExtension.class)
public class CheckActionTest {

    @Mock
    private ChatIdService chatIdService;
    @InjectMocks
    private CheckAction checkAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountNotExists() {
        var response = "Данный аккаунт Telegram не зарегистрирован";
        Message message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(chatIdString)).thenReturn(Optional.empty());

        assertThat(checkAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenAccountExists() {
        var username = "username";
        var email = "123@mail.ru";
        var sl = System.lineSeparator();
        var response = "Данные о вашем аккаунте:" + sl
                + "Имя: " + username + sl
                + "email: " + email;
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        var chatIdObj = new ChatId(1, chatIdString, username, email, false);
        when(chatIdService.findByChatId(chatIdString)).thenReturn(Optional.of(chatIdObj));

        assertThat(checkAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

}