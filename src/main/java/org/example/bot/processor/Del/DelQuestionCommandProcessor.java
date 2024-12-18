package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик команды удаления вопроса
 */
@Component
public class DelQuestionCommandProcessor extends AbstractCommandProcessor {

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
     * Утилита с вспомогательными методами
     */
    private final Util util;

    /**
     * Конструктор для инициализации обработчика команды удаления теста.
     *
     * @param stateService    сервис для управления состояниями
     * @param contextService  сервис для управления тестами
     * @param questionService cервис для управления вопросами
     * @param util утилита с вспомогательными методами
     */
    public DelQuestionCommandProcessor(StateService stateService,
                                       ContextService contextService,
                                       QuestionService questionService,
                                       Util util) {
        super("/del_question");
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String questionIdStr = parts[1];
            if (!util.isNumber(questionIdStr)) {
                return "Некорректный формат id вопроса. Пожалуйста, введите число.";
            }
            Long questionId = Long.parseLong(questionIdStr);
            Optional<QuestionEntity> questionOpt = questionService.getQuestion(questionId);

            return questionOpt.map(question -> {
                contextService.setCurrentQuestion(userId, question);
                stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
                return String.format("Вопрос “%s” будет удалён, вы уверены? (Да/Нет)", question.getQuestion());
            }).orElse("Вопрос не найден!");
        }
        if (parts.length == 1) {
            stateService.changeStateById(userId, UserState.DELETE_QUESTION);
            return "Введите id вопроса для удаления:\n";
        }
        return "Ошибка ввода. Укажите корректный id теста.";
    }
}
