package org.example.bot.processor.Edit;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.util.AnswerUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Oбработчик состояния редактирования ответа
 */
@Component
public class EditAnswerOptionChoiceProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Утилита с вспомогательными методами для вопросов
     */
    private final AnswerUtils answerUtils;


    /**
     * Конструктор для инициализации обработчика редактирования ответа
     *
     * @param stateService   сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     * @param answerUtils    утилита с вспомогательными методами для вопросов
     */
    public EditAnswerOptionChoiceProcessor(StateService stateService,
                                           ContextService contextService,
                                           AnswerUtils answerUtils) {
        super(stateService, UserState.EDIT_ANSWER_OPTION_CHOICE);
        this.stateService = stateService;
        this.contextService = contextService;
        this.answerUtils = answerUtils;
    }

    @Override
    public String process(Long userId, String message) {
        Optional<QuestionEntity> optionalCurrentQuestion = contextService.getCurrentQuestion(userId);
        if (optionalCurrentQuestion.isEmpty()) {
            return "Вопрос не найден";
        }
        QuestionEntity currentQuestion = optionalCurrentQuestion.get();
        if (message.equals("1")) {
            stateService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
            return "Сейчас варианты ответа выглядят так\n"
                    + answerUtils.answersToString(currentQuestion.getAnswers())
                    + "\nКакой вариант ответа вы хотите изменить?";
        } else if (message.equals("2")) {
            stateService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
            return "Сейчас варианты ответа выглядят так:\n"
                    + answerUtils.answersToString(currentQuestion.getAnswers())
                    + "\nКакой вариант ответа вы хотите сделать правильным?";
        }
        return "Некорректный ввод";
    }
}
