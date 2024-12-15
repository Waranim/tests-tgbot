package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.states.UserState;
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
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;


    /**
     * Конструктор для инициализации обработчика редактирования ответа
     *
     * @param stateService   сервис для управления состояниями
     * @param sessionService сервис для управления сессиями
     * @param util           утилита с вспомогательными методами
     */
    public EditAnswerOptionChoiceProcessor(StateService stateService,
                                           SessionService sessionService,
                                           Util util) {
        super(stateService, UserState.EDIT_ANSWER_OPTION_CHOICE);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
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
