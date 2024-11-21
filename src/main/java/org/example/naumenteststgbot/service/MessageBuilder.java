package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Создание сообщений
 */
@Service
public class MessageBuilder {
    /**
     * Создать сообщение с ошибкой
     */
    public SendMessage createErrorMessage(String chatId, String errorMessage) {
        return createSendMessage(chatId, "Ошибка: " + errorMessage, null);
    }

    /**
     * Создать обычное сообщение
     */
    public SendMessage createSendMessage(String chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (markup != null) {
            message.setReplyMarkup(markup);
        }
        return message;
    }
}
