package ru.checkdev.notification.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.notification.NtfSrv;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.TgRun;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;
import ru.checkdev.notification.web.TemplateController;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Testing ChatIdService class
 *
 * @author Oleg Ershov
 * @since 21.01.2024
 */

@SpringBootTest(classes = NtfSrv.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ChatIdServiceTest {

    @Autowired
    private ChatIdService chatIdService;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    @MockBean
    private TgRun tgRun;

    @MockBean
    private TemplateController templateController;

    @Test
    public void whenSave() {
        ChatId chatId = chatIdService.save(new ChatId(0, "123456", "username", "email", true));
        List<ChatId> result = chatIdService.findAll();
        assertTrue(result.contains(chatId));
    }

    @Test
    public void whenFoundById() {
        ChatId chatId = chatIdService.save(new ChatId(0, "123456", "username", "email", true));
        Optional<ChatId> resultOptional = chatIdService.findByChatId(chatId.getTgChatId());
        ChatId result = resultOptional.get();
        assertEquals(result.getUsername(), chatId.getUsername());
        assertEquals(result.getEmail(), chatId.getEmail());
        assertEquals(result.isNotification(), chatId.isNotification());
    }

    @Test
    public void whenNotFoundById() {
        Optional<ChatId> result = chatIdService.findByChatId("123456");
        assertTrue(result.isEmpty());
    }
}