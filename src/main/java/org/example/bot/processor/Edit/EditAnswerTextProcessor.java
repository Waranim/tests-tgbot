package org.example.naumenteststgbot.processor.Edit;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования формулировки ответа
 */
@Component
public class EditAnswerTextProcessor extends AbstractStateProcessor {

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
     * Конструктор для инициализации обработчика редактирования формулировки ответа
     *
     * @param stateService    cервис для управления состояниями
     * @param sessionService  cервис для управления сессиями
     * @param questionService cервис для управления вопросами
     */
    public EditAnswerTextProcessor(StateService stateService,
                                   SessionService sessionService,
                                   QuestionService questionService) {
        super(stateService, UserState.EDIT_ANSWER_TEXT);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        int editingAnswerIndex = sessionService.getEditingAnswerIndex(userId);
        currentQuestion.getAnswers().get(editingAnswerIndex).setAnswerText(message);
        questionService.update(currentQuestion);
        stateService.changeStateById(userId, UserState.DEFAULT);
        return String.format("Формулировка изменена на “%s”", message);
    }
}
