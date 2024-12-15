package org.example.naumenteststgbot.processor.Del;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния подтверждения удаления вопроса
 */
@Component
public class ConfirmDelQuestion extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика подтверждения удаления теста.
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param questionService сервис для управления вопросами
     */
    public ConfirmDelQuestion(StateService stateService,
                              SessionService sessionService,
                              QuestionService questionService) {
        super(stateService, UserState.CONFIRM_DELETE_QUESTION);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        message = message.toLowerCase();
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        stateService.changeStateById(userId, UserState.DEFAULT);
        if (message.equals("да")) {
            sessionService.setCurrentQuestion(userId, null);
            questionService.delete(currentQuestion);
            return String.format("Вопрос “%s” из теста “%s” удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
        }

        return String.format("Вопрос “%s” из теста “%s” не удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
    }
}
