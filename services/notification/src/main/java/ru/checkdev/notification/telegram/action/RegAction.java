package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.service.ChatIdService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.Calendar;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final String urlSiteAuth;
    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = "";
        if (chatIdService.findByChatId(chatIdNumber).isPresent()) {
            text = "Данный аккаунт Telegram уже зарегистрирован на сайте";
            return new SendMessage(chatIdNumber, text);
        }
        text = "Введите ваше имя и email для регистрации в формате \"имя/email\":";
        return new SendMessage(chatIdNumber, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var data = message.getText();
        var text = "";
        var sl = System.lineSeparator();

        if (!tgConfig.checkFormat(data)) {
            text = "Некорректный формат данных." + sl
                    + "Попробуйте снова" + sl
                    + "/new";
            return new SendMessage(chatIdNumber, text);
        }
        var substrings = data.split("/");
        var name = substrings[0];
        var email = substrings[1];

        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " некорректный." + sl
                   + "Попробуйте снова." + sl
                   + "/new";
            return new SendMessage(chatIdNumber, text);
        }

        var password = tgConfig.getPassword();
        var person = new PersonDTO(email, password, name, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                   + "/start";
            return new SendMessage(chatIdNumber, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatIdNumber, text);
        }

        ChatId chatId = new ChatId(0, chatIdNumber, name, email);
        chatIdService.save(chatId);

        text = "Вы зарегистрированы: " + sl
               + "Логин: " + email + sl
               + "Пароль: " + password + sl
               + urlSiteAuth;
        return new SendMessage(chatIdNumber, text);
    }
}
