package org.example.bot.processor.Edit;

import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования теста.
 */
@Component
public class EditTestProcessor extends AbstractStateProcessor {
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;

    /**
     * Конструктор для инициализации обработчика редактирования теста.
     * 
     * @param stateService сервис для управления состояниями
     */
    public EditTestProcessor(StateService stateService) {
        super(stateService, UserState.EDIT_TEST);
        this.stateService = stateService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        if(message.equals("1")){
            stateService.changeStateById(userId, UserState.EDIT_TEST_TITLE);
            return new BotResponse("Введите новое название теста");
        }
        else if(message.equals("2")){
            stateService.changeStateById(userId, UserState.EDIT_TEST_DESCRIPTION);
            return  new BotResponse("Введите новое описание теста");
        }

        return new BotResponse("Некорректный ввод");
    }
}
