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
import ru.checkdev.notification.service.ChatIdService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.Map;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Testing ForgetAction class
 *
 * @author Oleg Ershov
 * @since 17.01.2024
 */

@ExtendWith(MockitoExtension.class)
class ForgetActionTest {

    @Mock
    private TgAuthCallWebClint authCallWebClint;
    @Mock
    private ChatIdService chatIdService;
    @Mock
    TgConfig tgConfig;
    @InjectMocks
    private ForgetAction forgetAction;
    private final long chatId = 123456;
    private final String chatIdString = String.valueOf(chatId);

    @Test
    public void whenAccountNotExists() {
        String response = "Данный аккаунт Telegram не зарегистрирован";
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.empty());
        assertThat(forgetAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }

    @Test
    public void whenPasswordReset() {
        Message message = mock(Message.class);
        ChatId chatIdObj = new ChatId(1, chatIdString, "username", "email", false);
        when(message.getChatId()).thenReturn(chatId);
        when(chatIdService.findByChatId(any())).thenReturn(Optional.of(chatIdObj));
        when(tgConfig.getPassword()).thenReturn("password");
        when(authCallWebClint.doPost(any(), any())).thenReturn(Mono.just(new Object()));
        when(tgConfig.getObjectToMap(any())).thenReturn(Map.of("ok", "ok"));
        var response = "Логин: " + chatIdObj.getEmail() + System.lineSeparator()
                + "Новый пароль: password";
        assertThat(forgetAction.callback(message)).isEqualTo(new SendMessage(chatIdString, response));
    }
}