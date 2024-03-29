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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing UnsubscribeAction class
 *
 * @author Oleg Ershov
 * @since 30.01.2024
 */
@ExtendWith(MockitoExtension.class)
class UnsubscribeActionTest {

    @Mock
    private ChatIdService chatIdService;
    @InjectMocks
    private UnsubscribeAction unsubscribeAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountNotExists() {
        var response = "Данный аккаунт Telegram не зарегистрирован";
        var message = mock(Message.class);

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.isReg(any())).thenReturn(false);

        assertThat(unsubscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenUnsubscribeSuccess() {
        var message = mock(Message.class);
        var chatIdObj = new ChatId(1, chatIdString, "username", "email", true, true);
        var response = "Вы отписаны от уведомлений";

        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.isReg(any())).thenReturn(true);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.of(chatIdObj));

        assertThat(unsubscribeAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

}