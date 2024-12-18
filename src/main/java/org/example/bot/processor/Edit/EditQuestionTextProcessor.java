package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик состояния редактирования формулировки вопроса
 */
@Component
public class EditQuestionTextProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика редактирования формулировки вопроса
     *
     * @param stateService    cервис для управления состояниями
     * @param contextService  cервис для управления контекстом
     * @param questionService cервис для управления вопросами
     */
    public EditQuestionTextProcessor(StateService stateService,
                                     ContextService contextService,
                                     QuestionService questionService) {
        super(stateService, UserState.EDIT_QUESTION_TEXT);
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        Optional<QuestionEntity> optionalCurrentQuestion = contextService.getCurrentQuestion(userId);
        if (optionalCurrentQuestion.isEmpty()) {
            return "Вопрос не найден";
        }
        QuestionEntity currentQuestion = optionalCurrentQuestion.get();
        currentQuestion.setQuestion(message);
        questionService.update(currentQuestion);
        stateService.changeStateById(userId, UserState.DEFAULT);

        return String.format("Текст вопроса изменен на “%s”", message);
    }
}
