package org.example.bot.telegram;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Ответ бота
 */
public class BotResponse {

    /**
     * Сообщение ответа
     */
    private final String message;

    /**
     * Inline кнопки
     */
    private final List<InlineKeyboardButton> buttons;

    /**
     * Редактировать ли сообщение
     */
    private final boolean isEdit;

    /**
     * Конструктор для ответа бота
     */
    public BotResponse(String message, List<InlineKeyboardButton> buttons, boolean isEdit) {
        this.message = message;
        this.buttons = buttons;
        this.isEdit = isEdit;
    }

    /**
     * Конструктор только с сообщением
     */
    public BotResponse(String message) {
        this.message = message;
        this.buttons = null;
        this.isEdit = false;
    }

    /**
     * Получить сообщение
     */
    public String getMessage() {
        return message;
    }

    /**
     * Редактировать ли сообщение
     */
    public boolean isEdit() {
        return isEdit;
    }

    /**
     * Преобразует BotResponse в соответствующий метод Telegram API
     *
     * @param chatId ID чата
     * @param messageId ID сообщения (для редактирования)
     * @return SendMessage или EditMessageText в зависимости от isEdit
     */
    public BotApiMethod<?> convertToMessage(String chatId, Integer messageId) {
        InlineKeyboardMarkup markup = null;
        if (buttons != null && !buttons.isEmpty()) {
            markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (InlineKeyboardButton button : buttons) {
                keyboard.add(List.of(button));
            }
            markup.setKeyboard(keyboard);
        }

        if (isEdit && messageId != null) {
            return EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(message)
                    .replyMarkup(markup)
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .replyMarkup(markup)
                    .build();
        }
    }
}
