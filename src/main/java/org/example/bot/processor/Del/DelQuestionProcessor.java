package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик состояния удаления вопроса
 */
@Component
public class DelQuestionProcessor extends AbstractStateProcessor {

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
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика удаления вопроса
     *
     * @param stateService    сервис для управления состояниями
     * @param contextService  сервис для управления контекстом
     * @param questionService сервис для управления вопросами
     */
    public DelQuestionProcessor(StateService stateService,
                                ContextService contextService,
                                QuestionService questionService, ButtonUtils buttonUtils) {
        super(stateService, UserState.DELETE_QUESTION);
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        try {
            Long questionId = Long.parseLong(message);
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(buttonUtils.createButton("Да", "DEL_QUESTION_CONFIRM " + questionId + " да"));
            buttons.add(buttonUtils.createButton("Нет", "DEL_QUESTION_CONFIRM " + questionId + " нет"));
            Optional<QuestionEntity> questionOpt = questionService.getQuestion(questionId);
            return new BotResponse(questionOpt.map(question -> {
                contextService.setCurrentQuestion(userId, question);
                stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
                return String.format("Вопрос “%s” будет удалён, вы уверены?", question.getQuestion());
            }).orElse("Вопрос не найден!"), buttons, false);

        } catch (NumberFormatException e) {
            return new BotResponse("Некорректный формат идентификатора вопроса. Пожалуйста, введите число.");
        }
    }
}
