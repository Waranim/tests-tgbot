package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика удаления вопроса
     *
     * @param stateService    сервис для управления состояниями
     * @param contextService  сервис для управления контекстом
     * @param questionService сервис для управления вопросами
     */
    public DelQuestionProcessor(StateService stateService,
                                ContextService contextService,
                                QuestionService questionService) {
        super(stateService, UserState.DELETE_QUESTION);
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        try {
            Long questionId = Long.parseLong(message);
            Optional<QuestionEntity> questionOpt = questionService.getQuestion(questionId);
            return new BotResponse(questionOpt.map(question -> {
                contextService.setCurrentQuestion(userId, question);
                stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
                return String.format("Вопрос “%s” будет удалён, вы уверены? (Да/Нет)", question.getQuestion());
            }).orElse("Вопрос не найден!"));

        } catch (NumberFormatException e) {
            return new BotResponse("Некорректный формат идентификатора вопроса. Пожалуйста, введите число.");
        }
    }
}
