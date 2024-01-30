package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.service.ChatIdService;

import java.util.Optional;

/**
 * Класс реализует пункт отписки от уведомлений в телеграм бот
 *
 * @author Oleg Ershov
 * @since 30.01.24
 */
@AllArgsConstructor
public class UnsubscribeAction implements Action {

    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = NOT_REGISTERED;
        if (!chatIdService.isReg(chatIdNumber)) {
            return new SendMessage(chatIdNumber, text);
        }
        var chatId = chatIdService.findByChatId(chatIdNumber).get();
        if (chatId.isNotification()) {
            chatId.setNotification(false);
            chatIdService.save(chatId);
        }
        text = "Вы отписаны от уведомлений";
        return new SendMessage(chatIdNumber, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
