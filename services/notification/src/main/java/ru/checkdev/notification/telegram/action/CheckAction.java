package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.service.ChatIdService;
import java.util.Optional;

/**
 * Класс реализует пункт меню получения информации о привязанном аккаунте в телеграм бот
 *
 * @author Oleg Ershov
 * @since 30.01.2024
 */
@AllArgsConstructor
public class CheckAction implements Action {

    private final ChatIdService chatIdService;

    /**
     * Метод формирует ответ пользователю.
     * В случае, если данный аккаунт не зарегистрирован - информируем об этом пользователя
     * Если аккаунт зарегистрирован - отправляем привязанные к нему username и email
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = NOT_REGISTERED;
        var sl = System.lineSeparator();
        if (!chatIdService.isReg(chatIdNumber)) {
            return new SendMessage(chatIdNumber, text);
        }
        var chatId = chatIdService.findByChatId(chatIdNumber).get();
        text = "Данные о вашем аккаунте:" + sl
                + "Имя: " + chatId.getUsername() + sl
                + "email: " + chatId.getEmail();
        return new SendMessage(chatIdNumber, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
