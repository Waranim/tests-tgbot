package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования формулировки вопроса
 */
@Component
public class EditQuestionTextProcessor extends AbstractStateProcessor {

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
     * Конструктор для инициализации обработчика редактирования формулировки вопроса
     *
     * @param stateService    cервис для управления состояниями
     * @param sessionService  cервис для управления сессиями
     * @param questionService cервис для управления вопросами
     */
    public EditQuestionTextProcessor(StateService stateService,
                                     SessionService sessionService,
                                     QuestionService questionService) {
        super(stateService, UserState.EDIT_QUESTION_TEXT);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        currentQuestion.setQuestion(message);
        questionService.update(currentQuestion);
        stateService.changeStateById(userId, UserState.DEFAULT);
        return String.format("Текст вопроса изменен на “%s”", message);
    }
}
