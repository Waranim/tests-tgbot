package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для создания inline клавиатур
 */
@Service
public class KeyboardService {
    /**
     * Создать inline клавиатуру с несколькими кнопками
     * @param buttonsText текст кнопок
     * @param buttonsCallbackData данные для callback
     * @param prefix префикс для callback
     * @return сообщение с inline-клавиатурой
     */
    public InlineKeyboardMarkup createReply(List<String> buttonsText, List<String> buttonsCallbackData, String prefix) {
        if (buttonsText.size() != buttonsCallbackData.size()) {
            throw new IllegalArgumentException("Размер списков buttonsText и buttonsCallbackData должен совпадать");
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < buttonsText.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonsText.get(i));
            String callbackData = prefix.isEmpty()
                    ? buttonsCallbackData.get(i)
                    : prefix + " " + buttonsCallbackData.get(i);
            button.setCallbackData(callbackData);
            rowsInline.add(List.of(button));
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    /**
     * Создать inline клавиатуру с одной кнопкой
     * @param buttonName текст кнопки
     * @param buttonCallbackData данные для callback
     * @param prefix префикс для callback
     * @return сообщение с inline-клавиатурой
     */
    public InlineKeyboardMarkup createReply(String buttonName, String buttonCallbackData, String prefix) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        String callbackData = prefix.isEmpty()
                ? buttonCallbackData
                : prefix + " " + buttonCallbackData;
            button.setCallbackData(callbackData);
            rowsInline.add(List.of(button));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }
}
