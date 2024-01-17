package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.service.ChatIdService;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Testing RegAction class
 *
 * @author Oleg Ershov
 * @since 16.01.2024
 */

@ExtendWith(MockitoExtension.class)
public class RegActionTest {

    @Mock
    private ChatIdService chatIdService;
    @InjectMocks
    private RegAction regAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountAlreadyExists() {
        String response = "Данный аккаунт Telegram уже зарегистрирован на сайте";
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.of(new ChatId()));
        assertThat(regAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenAccountNotExists() {
        String response = "Введите ваше имя и email для регистрации в формате \"имя/email\":";
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.empty());
        assertThat(regAction.handle(message)).isEqualTo(new SendMessage(chatIdString, response));
    }
}