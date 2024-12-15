package org.example.naumenteststgbot.processor.Del;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;

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
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

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
     * @param sessionService  сервис для управления тестами
     * @param questionService cервис для управления вопросами
     * @param util утилита с вспомогательными методами
     */
    public DelQuestionCommandProcessor(StateService stateService,
                                       SessionService sessionService,
                                       QuestionService questionService, Util util) {
        super("/del_question");
        this.stateService = stateService;
        this.sessionService = sessionService;
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
            QuestionEntity question = questionService.getQuestion(questionId);
            if (question == null) {
                return "Вопрос не найден!";
            }
            sessionService.setCurrentQuestion(userId, question);
            stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
            return String.format("Вопрос “%s” будет удалён, вы уверены? (Да/Нет)", question.getQuestion());
        }
        if (parts.length == 1) {
            stateService.changeStateById(userId, UserState.DELETE_QUESTION);
            return "Введите id вопроса для удаления:\n";
        }
        return "Ошибка ввода. Укажите корректный id теста.";
    }
}
