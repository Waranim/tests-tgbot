package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * Создать inline клавиатуру для вопросов теста
     *
     * @param correctAnswerCount количество верно решённых вопросов
     * @param buttonsText дополнительный текст к кнопкам
     * @param buttonsCallbackData данные для callback
     * @param prefix префикс для callback
     * @param hiddenNextButton скрытие кнопки перехода на след. вопрос
     * @return inline-клавиатура
     */
    public InlineKeyboardMarkup createKeyboardForTest(int correctAnswerCount, List<String> buttonsText, List<String> buttonsCallbackData, String prefix, boolean hiddenNextButton) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        if (buttonsText == null) {
            buttonsText = new ArrayList<>(Collections.nCopies(buttonsCallbackData.size(), ""));
        }

        for (int i = 0; i < buttonsCallbackData.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.format("%d%s", i + 1, buttonsText.get(i)));
            String callbackData = prefix.isEmpty()
                    ? buttonsCallbackData.get(i)
                    : prefix + " " + buttonsCallbackData.get(i);
            button.setCallbackData(callbackData);
            rowsInline.add(List.of(button));
        }

        if (prefix.isEmpty() && !hiddenNextButton) {
            InlineKeyboardButton nextQuestion = new InlineKeyboardButton();
            nextQuestion.setText("След. вопрос");
            nextQuestion.setCallbackData("EDIT TEST NEXT");
            rowsInline.add(List.of(nextQuestion));
        }

        InlineKeyboardButton counter = new InlineKeyboardButton();
        counter.setText(String.format("Верно: %d", correctAnswerCount));
        counter.setCallbackData("COUNTER");
        rowsInline.add(List.of(counter));

        InlineKeyboardButton exit = new InlineKeyboardButton();
        InlineKeyboardButton finish = new InlineKeyboardButton();

        exit.setText("Выйти");
        exit.setCallbackData("TEST EXIT");

        finish.setText("Завершить");
        finish.setCallbackData("TEST FINISH");
        rowsInline.add(List.of(exit, finish));

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }
}
