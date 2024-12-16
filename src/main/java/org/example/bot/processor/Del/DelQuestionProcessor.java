package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния удаления вопроса
 */
@Component
public class DelQuestionProcessor extends AbstractStateProcessor {

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
     * Конструктор для инициализации обработчика удаления вопроса
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param questionService сервис для управления вопросами
     */
    public DelQuestionProcessor(StateService stateService,
                                SessionService sessionService,
                                QuestionService questionService) {
        super(stateService, UserState.DELETE_QUESTION);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity question = questionService.getQuestion(Long.parseLong(message));
        sessionService.setCurrentQuestion(userId, question);
        stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
        if (question == null)
            return "Вопрос не найден!";

        return String.format("Вопрос “%s” будет удалён, вы уверены? (Да/Нет)",
                question.getQuestion());
    }
}
