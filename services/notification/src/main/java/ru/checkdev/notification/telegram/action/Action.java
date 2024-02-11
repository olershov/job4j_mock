package ru.checkdev.notification.telegram.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * 3. Мидл
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 24.01.2024
 */
public interface Action {

    static final String NOT_REGISTERED = "Данный аккаунт Telegram не зарегистрирован";
    static final String ERROR_OBJECT = "error";

    BotApiMethod<Message> handle(Message message);

    BotApiMethod<Message> callback(Message message);
}
