package org.example.naumenteststgbot.processor.Add;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния добавления названия вопроса
 */
@Component
public class AddQuestionTextProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Сервис для управления тестами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика добавления названия вопроса
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param questionService сервис для управления тестами
     */
    public AddQuestionTextProcessor(StateService stateService,
                                    SessionService sessionService,
                                    QuestionService questionService) {
        super(stateService, UserState.ADD_QUESTION_TEXT);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        currentQuestion.setQuestion(message);
        questionService.update(currentQuestion);
        stateService.changeStateById(userId, UserState.ADD_ANSWER);
        return "Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.";
    }
}
