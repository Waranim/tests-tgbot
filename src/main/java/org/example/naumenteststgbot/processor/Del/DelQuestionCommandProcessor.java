package org.example.naumenteststgbot.processor.Del;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
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
     * Конструктор для инициализации обработчика команды удаления теста.
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления тестами
     * @param questionService cервис для управления вопросами
     */
    public DelQuestionCommandProcessor(StateService stateService,
                                       SessionService sessionService,
                                       QuestionService questionService) {
        super("/del_question");
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        UserSession userSession = sessionService.getSession(userId);
        if (userSession.getState() == UserState.CONFIRM_DELETE_QUESTION) {
            QuestionEntity question = userSession.getCurrentQuestion();
            if (question == null) {
                return "Вопрос не найден!";
            }
            message = message.toLowerCase();
            if (message.equals("да")) {
                sessionService.setCurrentQuestion(userId, null);
                questionService.delete(question);
                stateService.changeStateById(userId, UserState.DEFAULT);
                return "Вопрос успешно удален.";
            } else if (message.equals("нет")) {
                stateService.changeStateById(userId, UserState.DEFAULT);
                return "Удаление вопроса отменено.";
            } else {
                return "Некорректный ввод. Пожалуйста, введите 'Да' или 'Нет'.";
            }
        }
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String questionIdStr = parts[1];
            if (!questionIdStr.matches("^\\d+$")) {
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
