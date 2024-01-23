package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.Calendar;
import java.util.Optional;

/**
 * Класс реализует пункт меню восстановления пароля в телеграм бот
 *
 * @author Oleg Ershov
 * @since 24.01.24
 */
@Slf4j
@AllArgsConstructor
public class ForgetAction implements Action {

    private static final String URL_AUTH_FORGOT = "/forgot";
    private final TgAuthCallWebClint authCallWebClint;
    private final TgConfig tgConfig;
    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var sl = System.lineSeparator();
        var text = "";
        Optional<ChatId> chatIdOptional = chatIdService.findByChatId(chatIdNumber);
        if (chatIdOptional.isEmpty()) {
            text = NOT_REGISTERED;
            return new SendMessage(chatIdNumber, text);
        }
        var email = chatIdOptional.get().getEmail();
        var password = tgConfig.getPassword();
        var person = new PersonDTO(email, password, null, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_FORGOT, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatIdNumber, text);
        }
        var mapObject = tgConfig.getObjectToMap(result);
        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка восстановления пароля: обратитесь в поддержку ";
            return new SendMessage(chatIdNumber, text);
        }
        text = "Логин: " + email + sl
                + "Новый пароль: " + password;
        return new SendMessage(chatIdNumber, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
