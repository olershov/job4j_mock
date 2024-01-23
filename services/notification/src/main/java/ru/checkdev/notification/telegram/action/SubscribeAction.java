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

import static ru.checkdev.notification.telegram.config.TgConfig.DELIMITER;

/**
 * Класс реализует пункт подписки на уведомления в телеграм бот
 *
 * @author Oleg Ershov
 * @since 24.01.24
 */
@Slf4j
@AllArgsConstructor
public class SubscribeAction implements Action {
    private final ChatIdService chatIdService;
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    public static final String ENTER_EMAIL_AND_PASSWORD =
            "Введите ваш email и пароль для подписки на уведомления в формате \"email" + DELIMITER + "password\":";
    private static final String FIND_PERSON = "/person/check";

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = NOT_REGISTERED;
        Optional<ChatId> chatIdOptional = chatIdService.findByChatId(chatIdNumber);
        if (chatIdOptional.isEmpty()) {
            return new SendMessage(chatIdNumber, text);
        }
        text = ENTER_EMAIL_AND_PASSWORD;
        return new SendMessage(chatIdNumber, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var data = message.getText();
        var text = "";
        var sl = System.lineSeparator();

        var emailAndPassword = tgConfig.parseEmailAndPassword(data);
        if (emailAndPassword.isEmpty()) {
            text = "Некорректный формат данных." + sl
                    + "Попробуйте снова" + sl
                    + "/subscribe";
            return new SendMessage(chatIdNumber, text);
        }

        var email = emailAndPassword.get("email");
        var password = emailAndPassword.get("password");

        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " некорректный." + sl
                    + "Попробуйте снова." + sl
                    + "/subscribe";
            return new SendMessage(chatIdNumber, text);
        }

        var person = new PersonDTO(email, password, null, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(FIND_PERSON, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatIdNumber, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка оформления подписки: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatIdNumber, text);
        }

        ChatId chatId = chatIdService.findByChatId(chatIdNumber).get();
        chatId.setNotification(true);
        chatIdService.save(chatId);
        text = "Вы подписаны на уведомления";

        return new SendMessage(chatIdNumber, text);
    }
}
