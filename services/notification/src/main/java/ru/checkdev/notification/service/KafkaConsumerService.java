package ru.checkdev.notification.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.Notify;

@Service
@AllArgsConstructor
public class KafkaConsumerService {

    private final TemplateService templateService;

    @KafkaListener(topics = "notify")
    public void sendNotify(Notify notify) {
        templateService.send(notify);
    }
}
