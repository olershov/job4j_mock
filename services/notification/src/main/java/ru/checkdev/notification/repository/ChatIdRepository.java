package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.ChatId;
import java.util.Optional;

/**
 * @author Oleg Ershov
 * @since 15.01.2024
 */
public interface ChatIdRepository extends CrudRepository<ChatId, Integer> {

    Optional<ChatId> findByTgChatId(String chatId);

}
