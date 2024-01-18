package ru.checkdev.notification.telegram.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Класс реализует пункт подписки на уведомления в телеграм бот
 *
 * @author Oleg Ershov
 * @since 17.01.24
 */
public class SubscribeAction implements Action {

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return null;
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return null;
    }
}
