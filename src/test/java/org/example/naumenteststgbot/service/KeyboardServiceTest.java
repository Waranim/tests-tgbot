package org.example.naumenteststgbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для KeyboardService
 */
class KeyboardServiceTest {

    /**
     * Сервис создания inline-клавиатуры
     */
    private KeyboardService keyboardService;

    /**
     * Инициализация перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        keyboardService = new KeyboardService();
    }

    /**
     * Тест на корректное создание клавиатуры с валидными параметрами
     */
    @Test
    void testCreateKeyboardForTestWithValidInput() {
        List<String> buttonsText = List.of("A", "B", "C");
        List<String> buttonsCallbackData = List.of("1", "2", "3");
        InlineKeyboardMarkup keyboard = keyboardService.createKeyboardForTest(2, buttonsText, buttonsCallbackData, "", false);

        assertNotNull(keyboard);
        assertEquals(6, keyboard.getKeyboard().size());

        InlineKeyboardButton firstButton = keyboard.getKeyboard().getFirst().getFirst();
        assertEquals("1A", firstButton.getText());
        assertEquals("1", firstButton.getCallbackData());

        InlineKeyboardButton nextButton = keyboard.getKeyboard().get(3).getFirst();
        assertEquals("След. вопрос", nextButton.getText());
        assertEquals("EDIT TEST NEXT", nextButton.getCallbackData());

        InlineKeyboardButton counterButton = keyboard.getKeyboard().get(4).getFirst();
        assertEquals("Верно: 2", counterButton.getText());
    }

    /**
     * Тест на скрытие кнопки "След. вопрос"
     */
    @Test
    void testCreateKeyboardForTestWithHiddenNextButton() {
        List<String> buttonsText = List.of("A", "B");
        List<String> buttonsCallbackData = List.of("1", "2");
        InlineKeyboardMarkup keyboard = keyboardService.createKeyboardForTest(1, buttonsText, buttonsCallbackData, "", true);

        assertNotNull(keyboard);
        assertEquals(4, keyboard.getKeyboard().size());
    }

    /**
     * Тест на добавление префикса к callback данным
     */
    @Test
    void testCreateKeyboardForTest_withPrefix() {
        List<String> buttonsCallbackData = List.of("callback1", "callback2");
        InlineKeyboardMarkup keyboard = keyboardService.createKeyboardForTest(0, null, buttonsCallbackData, "PREFIX", false);

        assertNotNull(keyboard);
        assertEquals("1", keyboard.getKeyboard().getFirst().getFirst().getText().substring(0, 1));
        assertEquals("PREFIX callback1", keyboard.getKeyboard().getFirst().getFirst().getCallbackData());
    }

    /**
     * Тест на обработку null в качестве текста кнопок
     */
    @Test
    void testCreateKeyboardForTest_withEmptyText() {
        List<String> buttonsCallbackData = List.of("data1");
        InlineKeyboardMarkup keyboard = keyboardService.createKeyboardForTest(0, null, buttonsCallbackData, "", false);

        assertEquals("1", keyboard.getKeyboard().getFirst().getFirst().getText());
        assertEquals("data1", keyboard.getKeyboard().getFirst().getFirst().getCallbackData());
    }
}