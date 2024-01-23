package ru.checkdev.notification.telegram.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.ChatId;

import java.util.List;
import java.util.Optional;

/**
 * @author Oleg Ershov
 * @since 15.01.2024
 */
public interface ChatIdRepository extends CrudRepository<ChatId, Integer> {

    List<ChatId> findAll();

    Optional<ChatId> findByTgChatId(String chatId);

}
