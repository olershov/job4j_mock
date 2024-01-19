package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Oleg Ershov
 * @since 15.01.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "chat_id")
public class ChatId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tg_chat_id")
    private String tgChatId;
    private String username;
    private String email;
    private boolean notification;

}
