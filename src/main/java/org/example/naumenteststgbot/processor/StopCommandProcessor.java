package org.example.naumenteststgbot.processor;

import org.example.naumenteststgbot.entity.AnswerEntity;
import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * Обработчик команды /stop
 */
@Component
public class StopCommandProcessor extends AbstractCommandProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;

    /**
     * Конструктор для инициализации обработчика команды /stop
     *
     * @param stateService   сервис для управления состояниями
     * @param sessionService сервис для управления сессиями
     * @param util           утилита с вспомогательными методами
     */
    public StopCommandProcessor(StateService stateService, SessionService sessionService, Util util) {
        super("/stop");
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.util = util;

    }

    @Override
    public String process(Long userId, String message) {
        UserState userState = sessionService.getSession(userId).getState();
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        if (currentQuestion == null) {
            return "Нет текущего вопроса. Пожалуйста, выберите или создайте вопрос.";
        }
        List<AnswerEntity> answers = currentQuestion.getAnswers();
        if (userState == UserState.ADD_ANSWER) {
            if (answers.size() < 2) {
                return "Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.";
            }
            stateService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
            return "Укажите правильный вариант ответа:\n" + util.answersListToString(currentQuestion.getAnswers());
        }
        return "Команда /stop используется только при создании вопроса";
    }
}