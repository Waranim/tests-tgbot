package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик callback query
 */
@Service
public class CallbackQueryHandler {
    /**
     * Сервис для тестов
     */
    private final TestService testService;

    public CallbackQueryHandler(TestService testService) {
        this.testService = testService;
    }

    /**
     * Обработать callback query
     */
    public SendMessage handle(Update update) {
        SendMessage sendMessage;
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();
        String[] callbackDataParts = callbackData.split(" ");
        switch (callbackDataParts[0]) {
            case "TEST":
                sendMessage = testService.handleCallback(update);
                break;
            default:
                sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Ошибка!");
                break;
        }

        return sendMessage;
    }

    public EditMessageText handleEdit(Update update) {
        EditMessageText editMessageText;
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();
        String[] callbackDataParts = callbackData.split(" ");
        switch (callbackDataParts[1]) {
            case "TEST":
                editMessageText = testService.handleCallbackEdit(update);
                break;
            case "IGNORE":
                return null;
            default:
                editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText("Ошибка!");
                break;
        }

        return editMessageText;
    }
}
