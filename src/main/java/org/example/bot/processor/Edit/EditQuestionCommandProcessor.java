package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.states.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды редактирования вопрса
 */
@Component
public class EditQuestionCommandProcessor extends AbstractCommandProcessor {

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
     * Конструктор для инициализации обработчика команды редактирования вопроса
     *
     * @param stateService    cервис для управления состояниями
     * @param sessionService  cервис для управления сессиями
     * @param questionService cервис для управления вопросами
     * @param util утилита с вспомогательными методами
     */
    public EditQuestionCommandProcessor(StateService stateService,
                                        SessionService sessionService,
                                        QuestionService questionService, Util util) {
        super("/edit_question");
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return "Используйте команду вместе с идентификатором вопроса!";
        }
        if (!util.isNumber(parts[1])) {
            return "Ошибка ввода. Укажите корректный id теста.";
        }
        Long questionId = Long.parseLong(parts[1]);
        QuestionEntity question = questionService.getQuestion(questionId);
        if (question == null) {
            return "Вопрос не найден!";
        }
        sessionService.setCurrentQuestion(userId, question);
        stateService.changeStateById(userId, UserState.EDIT_QUESTION);
        return String.format("""
                Вы выбрали вопрос “%s”. Что вы хотите изменить в вопросе?
                1: Формулировку вопроса
                2: Варианты ответа
                """, question.getQuestion());
    }
}
