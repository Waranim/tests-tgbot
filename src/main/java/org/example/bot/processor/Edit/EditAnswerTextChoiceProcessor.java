package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
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
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Конструктор для инициализации обработчика редактирования формулировки ответа
     *
     * @param stateService   cервис для управления состояниями
     * @param contextService cервис для управления контекстом
     */
    public EditAnswerTextChoiceProcessor(StateService stateService,
                                         ContextService contextService) {
        super(stateService, UserState.EDIT_ANSWER_TEXT_CHOICE);
        this.stateService = stateService;
        this.contextService = contextService;
    }

    @Override
    public String process(Long userId, String message) {
        QuestionEntity currentQuestion = contextService.getCurrentQuestion(userId);
        int answerIndex = Integer.parseInt(message) - 1;
        if (answerIndex < 0 || answerIndex >= currentQuestion.getAnswers().size()) {
            return "Некорректный номер ответа. Попробуйте еще раз.";
        }
        contextService.setEditingAnswerIndex(userId, answerIndex);
        stateService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT);
        return "Введите новую формулировку ответа";
    }
}
