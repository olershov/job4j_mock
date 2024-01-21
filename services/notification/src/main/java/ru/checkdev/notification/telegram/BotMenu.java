package ru.checkdev.notification.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.checkdev.notification.telegram.action.Action;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 3. Мидл
 * Реализация меню телеграм бота.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 21.01.2024
 */
public class BotMenu extends TelegramLongPollingBot {
    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();
    private final Map<String, Action> actions;
    private final String username;
    private final String token;
    private static final List<String> WAIT_RESPONSE = List.of(
            "Введите ваше имя и email для регистрации в формате \"имя/email\":"
    );


    public BotMenu(Map<String, Action> actions, String username, String token) throws TelegramApiException {
        this.actions = actions;
        this.username = username;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }


    /**
     * Метод выполняет Action и отправляет результат пользователю.
     * 1. Проверка, существует ли введённая пользователем команда.
     * 2. Если существует - вызываем метод handle() у соответствующего Action. Добавляем в bindingBy,
     * если пользователь получает сообщение о необходимости ввода данных
     * 3. Если команда не существует - проверяем, есть ли значение в bindingBy по ключу chatId:
     * если есть - вызываем метод callback у соответствующего Action и удаляем значение из bondingBy,
     * если нет - возвращаем сообщение о том, что такой команды нет
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param update Update
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var key = update.getMessage().getText();
            var chatId = update.getMessage().getChatId().toString();
            if (actions.containsKey(key)) {
                var msg = (SendMessage) actions.get(key).handle(update.getMessage());
                if (WAIT_RESPONSE.contains(msg.getText())) {
                    bindingBy.put(chatId, key);
                }
                send(msg);
            } else if (bindingBy.containsKey(chatId)) {
                var msg = actions.get(bindingBy.get(chatId)).callback(update.getMessage());
                bindingBy.remove(chatId);
                send(msg);
            } else {
                var msg = new SendMessage(chatId, "Команда не поддерживается! Список доступных команд: /start");
                send(msg);
            }
        }
    }

    private void send(BotApiMethod msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
