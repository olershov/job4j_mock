package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.ChatId;
import ru.checkdev.notification.telegram.repository.ChatIdRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Oleg Ershov
 * @since 15.01.2024
 */
@Service
@AllArgsConstructor
public class ChatIdService {

    private final ChatIdRepository repository;

    public ChatId save(ChatId chatId) {
        return repository.save(chatId);
    }

    public Optional<ChatId> findByChatId(String chatId) {
        return repository.findByTgChatId(chatId);
    }

    public List<ChatId> findAll() {
        return repository.findAll();
    }

}
