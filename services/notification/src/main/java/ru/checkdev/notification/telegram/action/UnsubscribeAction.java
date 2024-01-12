package ru.checkdev.notification.telegram.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UnsubscribeAction implements Action {

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return null;
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return null;
    }
}
