package org.example.naumenteststgbot.processor.Add;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.processor.StopCommandProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния добавления ответа в вопрос
 */
@Component
public class AddAnswerQuestionProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Обработчик команды /stop
     */
    private final StopCommandProcessor stopCommandProcessor;

    /**
     * Конструктор для инициализации обработчика состояния добавления ответа в вопрос
     *
     * @param stateService         сервис для управления состоянием
     * @param sessionService       сервис для управления сессиями
     * @param questionService      сервис для управления вопросами
     * @param stopCommandProcessor обработчик команды /stop
     */
    public AddAnswerQuestionProcessor(StateService stateService,
                                      SessionService sessionService,
                                      QuestionService questionService, StopCommandProcessor stopCommandProcessor) {
        super(stateService, UserState.ADD_ANSWER);
        this.sessionService = sessionService;
        this.questionService = questionService;
        this.stopCommandProcessor = stopCommandProcessor;
    }

    @Override
    public String process(Long userId, String message) {
        if (message.equals("/stop")) {
            return stopCommandProcessor.process(userId, message);
        }
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        questionService.addAnswerOption(currentQuestion, message);
        return "Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду “/stop”.";
    }
}
