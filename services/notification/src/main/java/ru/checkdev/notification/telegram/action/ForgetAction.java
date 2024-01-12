package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.Calendar;

/**
 * Класс реализует пункт меню восстановления пароля в телеграм бот
 *
 * @author Oleg Ershov
 * @since 12.01.24
 */
@Slf4j
@AllArgsConstructor
public class ForgetAction implements Action {

    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_FORGOT = "/forgot";
    private final TgAuthCallWebClint authCallWebClint;
    private final TgConfig tgConfig = new TgConfig("tg/", 8);

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email для восстановления пароля:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var email = message.getText();
        var text = "";
        return new SendMessage(chatId, text);
    }
}
