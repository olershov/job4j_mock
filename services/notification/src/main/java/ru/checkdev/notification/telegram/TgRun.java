package ru.checkdev.notification.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.checkdev.notification.telegram.service.ChatIdService;
import ru.checkdev.notification.telegram.action.*;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import java.util.List;
import java.util.Map;

/**
 * 3. Мидл
 * Инициализация телеграм бот,
 * username = берем из properties
 * token = берем из properties
 *
 * @author Dmitry Stepanov, user Dmitry
 * @author Oleg Ershov
 * @since 24.01.2024
 */
@Component
@Slf4j
public class TgRun {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final ChatIdService chatIdService;
    private final TgConfig tgConfig = new TgConfig();
    @Value("${tg.username}")
    private String username;
    @Value("${tg.token}")
    private String token;
    @Value("${server.site.url.login}")
    private String urlSiteAuth;

    public TgRun(TgAuthCallWebClint tgAuthCallWebClint, ChatIdService chatIdService) {
        this.tgAuthCallWebClint = tgAuthCallWebClint;
        this.chatIdService = chatIdService;
    }

    @Bean
    public void initTg() {
        Map<String, Action> actionMap = Map.of(
                "/start", new InfoAction(List.of(
                        "/start - команды бота",
                        "/new - регистрация нового пользователя",
                        "/check - информация об аккаунте",
                        "/forget - восстановление пароля",
                        "/subscribe - подписаться на уведомления",
                        "/unsubscribe - отписаться от уведомлений")),
                "/new", new RegAction(tgConfig, tgAuthCallWebClint, urlSiteAuth, chatIdService),
                "/check", new CheckAction(chatIdService),
                "/forget", new ForgetAction(tgAuthCallWebClint, tgConfig, chatIdService),
                "/subscribe", new SubscribeAction(chatIdService, tgConfig, tgAuthCallWebClint),
                "/unsubscribe", new UnsubscribeAction(chatIdService)
        );
        try {
            BotMenu menu = new BotMenu(actionMap, username, token);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(menu);
        } catch (TelegramApiException e) {
            log.error("Telegram bot: {}, ERROR {}", username, e.getMessage());
        }
    }
}
