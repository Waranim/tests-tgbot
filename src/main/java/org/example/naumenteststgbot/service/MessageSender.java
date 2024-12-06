package org.example.naumenteststgbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Интерфейс для отправки сообщений
 */
@FunctionalInterface
public interface MessageSender {

    /**
     * Отправить сообщение
     * @param message сообщение, которое будет отправлено пользователю
     */
    void sendMessage(SendMessage message);
}
