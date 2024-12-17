package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
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
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика подтверждения удаления теста.
     *
     * @param stateService    сервис для управления состояниями
     * @param contextService  сервис для управления контекстом
     * @param questionService сервис для управления вопросами
     */
    public ConfirmDelQuestion(StateService stateService,
                              ContextService contextService,
                              QuestionService questionService) {
        super(stateService, UserState.CONFIRM_DELETE_QUESTION);
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        message = message.toLowerCase();
        QuestionEntity currentQuestion = contextService.getCurrentQuestion(userId);
        stateService.changeStateById(userId, UserState.DEFAULT);
        if (message.equals("да")) {
            contextService.setCurrentQuestion(userId, null);
            questionService.delete(currentQuestion);
            return String.format("Вопрос “%s” из теста “%s” удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
        }

        return String.format("Вопрос “%s” из теста “%s” не удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
    }
}
