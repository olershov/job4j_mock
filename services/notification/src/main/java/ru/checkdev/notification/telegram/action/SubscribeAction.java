package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.service.ChatIdService;

import java.util.Optional;

/**
 * Класс реализует пункт подписки на уведомления в телеграм бот
 *
 * @author Oleg Ershov
 * @since 17.01.24
 */
@AllArgsConstructor
public class SubscribeAction implements Action {

    private final ChatIdService chatIdService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatIdNumber = message.getChatId().toString();
        var text = "Данный аккаунт Telegram не зарегистрирован";
        Optional<ChatId> chatIdOptional = chatIdService.findByChatId(chatIdNumber);
        if (chatIdOptional.isEmpty()) {
            return new SendMessage(chatIdNumber, text);
        }
        var chatId = chatIdOptional.get();
        if (!chatId.isNotification()) {
            chatId.setNotification(true);
            chatIdService.save(chatId);
        }
        text = "Вы подписаны на уведомления";
        return new SendMessage(chatIdNumber, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
