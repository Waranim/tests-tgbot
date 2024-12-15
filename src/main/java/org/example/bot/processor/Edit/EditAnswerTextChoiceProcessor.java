package org.example.naumenteststgbot.processor.Edit;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования ответа
 */
@Component
public class EditAnswerTextChoiceProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Конструктор для инициализации обработчика редактирования формулировки ответа
     *
     * @param stateService   cервис для управления состояниями
     * @param sessionService cервис для управления сессиями
     */
    public EditAnswerTextChoiceProcessor(StateService stateService,
                                         SessionService sessionService) {
        super(stateService, UserState.EDIT_ANSWER_TEXT_CHOICE);
        this.stateService = stateService;
        this.sessionService = sessionService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = sessionService.getCurrentQuestion(userId);
        int answerIndex = Integer.parseInt(message) - 1;
        if (answerIndex < 0 || answerIndex >= currentQuestion.getAnswers().size()) {
            return "Некорректный номер ответа. Попробуйте еще раз.";
        }
        sessionService.setEditingAnswerIndex(userId, answerIndex);
        stateService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT);
        return "Введите новую формулировку ответа";
    }
}
