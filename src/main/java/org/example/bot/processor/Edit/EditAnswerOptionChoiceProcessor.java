package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;

/**
 * Oбработчик состояния редактирования ответа
 */
@Component
public class EditAnswerOptionChoiceProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;


    /**
     * Конструктор для инициализации обработчика редактирования ответа
     *
     * @param stateService   сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     * @param util           утилита с вспомогательными методами
     */
    public EditAnswerOptionChoiceProcessor(StateService stateService,
                                           ContextService contextService,
                                           Util util) {
        super(stateService, UserState.EDIT_ANSWER_OPTION_CHOICE);
        this.stateService = stateService;
        this.contextService = contextService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = contextService.getCurrentQuestion(userId);
        if (message.equals("1")) {
            stateService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
            return "Сейчас варианты ответа выглядят так\n" + util.answersListToString(currentQuestion.getAnswers())
                    + "\nКакой вариант ответа вы хотите изменить?";
        } else if (message.equals("2")) {
            stateService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
            return "Сейчас варианты ответа выглядят так:\n" + util.answersListToString(currentQuestion.getAnswers())
                    + "\nКакой вариант ответа вы хотите сделать правильным?";
        }
        return "Некорректный ввод";
    }
}
