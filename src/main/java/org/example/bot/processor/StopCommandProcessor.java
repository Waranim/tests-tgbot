package org.example.bot.processor;

import org.example.bot.entity.AnswerEntity;
import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.UserContext;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

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
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;

    /**
     * Конструктор для инициализации обработчика команды /stop
     *
     * @param stateService   сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     * @param util           утилита с вспомогательными методами
     */
    public StopCommandProcessor(StateService stateService,
                                ContextService contextService,
                                Util util) {
        super("/stop");
        this.stateService = stateService;
        this.contextService = contextService;
        this.util = util;

    }

    @Override
    public String process(Long userId, String message) {
        Optional<UserContext> optionalContext = contextService.getContext(userId);
        if (optionalContext.isEmpty()) {
            return "Ошибка";
        }
        UserState userState = optionalContext.get().getState();
        Optional<QuestionEntity> optionalCurrentQuestion = contextService.getCurrentQuestion(userId);
        if (optionalCurrentQuestion.isEmpty()) {
            return "Нет текущего вопроса. Пожалуйста, выберите или создайте вопрос.";
        }
        QuestionEntity currentQuestion = optionalCurrentQuestion.get();
        List<AnswerEntity> answers = currentQuestion.getAnswers();
        if (userState != UserState.ADD_ANSWER) {
            return "Команда /stop используется только при создании вопроса.";
        }
        if (answers.size() < 2) {
            return "Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.";
        }
        stateService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
        return "Укажите правильный вариант ответа:\n" + util.answersListToString(currentQuestion.getAnswers());
    }
}