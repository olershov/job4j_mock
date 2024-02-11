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

import static ru.checkdev.notification.telegram.config.TgConfig.DELIMITER;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 30.01.2024
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {
    private final TgConfig tgConfig;
    private final TgAuthCallWebClint authCallWebClint;
    private final String urlSiteAuth;
    private final ChatIdService chatIdService;
    public static final String ENTER_USERNAME_AND_EMAIL =
            "Введите ваше имя и email для регистрации в формате \"имя" + DELIMITER + "email\":";
    private static final String URL_AUTH_REGISTRATION = "/registration";
    private static final String ALREADY_REGISTERED = "Данный аккаунт Telegram уже зарегистрирован на сайте";

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = "";
        var chatIdOptional = chatIdService.findByChatId(chatIdNumber);
        if (chatIdOptional.isPresent()) {
            if (chatIdOptional.get().isReg()) {
                text = ALREADY_REGISTERED;
                return new SendMessage(chatIdNumber, text);
            }
        } else {
            var chatId = new ChatId();
            chatId.setTgChatId(chatIdNumber);
            chatIdService.save(chatId);
        }
        text = ENTER_USERNAME_AND_EMAIL;
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
        var nameAndEmail = tgConfig.parseUsernameAndEmail(data);
        if (nameAndEmail.isEmpty()) {
            text = "Некорректный формат данных." + sl
                    + "Попробуйте снова" + sl
                    + "/new";
            return new SendMessage(chatIdNumber, text);
        }
        var name = nameAndEmail.get("username");
        var email = nameAndEmail.get("email");

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

        var chatId = chatIdService.findByChatId(chatIdNumber).get();
        chatId.setEmail(email);
        chatId.setUsername(name);
        chatId.setReg(true);
        chatIdService.save(chatId);

        text = "Вы зарегистрированы: " + sl
               + "Логин: " + email + sl
               + "Пароль: " + password + sl
               + urlSiteAuth;
        return new SendMessage(chatIdNumber, text);
    }
}
