package org.example.bot.processor.Edit;

import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования вопроса
 */
@Component
public class EditQuestionProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;

    /**
     * Конструктор для инициализации обработчика редактирования вопроса
     *
     * @param stateService сервис для управления состояниями
     */
    public EditQuestionProcessor(StateService stateService) {
        super(stateService, UserState.EDIT_QUESTION);
        this.stateService = stateService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        if (message.equals("1")) {
            stateService.changeStateById(userId, UserState.EDIT_QUESTION_TEXT);
            return new BotResponse("Введите новый текст вопроса");
        } else if (message.equals("2")) {
            stateService.changeStateById(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
            return new BotResponse("Что вы хотите сделать с вариантом ответа?\n1: Изменить формулировку ответа\n2: Изменить правильность варианта ответа");
        }
        return new BotResponse("Некорректный ввод");
    }
}
