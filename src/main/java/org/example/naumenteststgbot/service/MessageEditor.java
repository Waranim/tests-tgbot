package org.example.naumenteststgbot.service;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Интерфейс для редактирования сообщений
 */
@FunctionalInterface
public interface MessageEditor {
    /**
     * Редактировать сообщение
     * @param message сообщение, которое будет отредактировано
     */
    void editMessage(EditMessageText message);
}
