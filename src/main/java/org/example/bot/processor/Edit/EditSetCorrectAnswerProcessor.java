package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.states.UserState;

import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования правильного ответа
 */
@Component
public class EditSetCorrectAnswerProcessor extends AbstractStateProcessor {

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
     * Конструктор для инициализации обработчика редактирования правильного ответа
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param questionService сервис для управления вопросами
     */
    public EditSetCorrectAnswerProcessor(StateService stateService,
                                         SessionService sessionService,
                                         QuestionService questionService) {
        super(stateService, UserState.SET_CORRECT_ANSWER);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        String setCorrectAnswer = questionService.setCorrectAnswer(currentQuestion, Integer.parseInt(message));
        if (!setCorrectAnswer.startsWith("Некорректный")) {
            stateService.changeStateById(userId, UserState.DEFAULT);
        }

        return setCorrectAnswer;
    }
}
