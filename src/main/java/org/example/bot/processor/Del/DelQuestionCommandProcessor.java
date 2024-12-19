package org.example.bot.processor.Del;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.example.bot.util.NumberUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик команды удаления вопроса
 */
@Component
public class DelQuestionCommandProcessor extends AbstractCommandProcessor {

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
     * Утилита с вспомогательными числовыми методами
     */
    private final NumberUtils numberUtils;
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика команды удаления теста.
     *
     * @param stateService    сервис для управления состояниями
     * @param contextService  сервис для управления тестами
     * @param questionService cервис для управления вопросами
     * @param numberUtils утилита с вспомогательными числовыми методами
     */
    public DelQuestionCommandProcessor(StateService stateService,
                                       ContextService contextService,
                                       QuestionService questionService,
                                       NumberUtils numberUtils, ButtonUtils buttonUtils) {
        super("/del_question");
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
        this.numberUtils = numberUtils;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String questionIdStr = parts[1];
            if (!numberUtils.isNumber(questionIdStr)) {
                return new BotResponse("Некорректный формат id вопроса. Пожалуйста, введите число.");
            }
            Long questionId = Long.parseLong(questionIdStr);
            Optional<QuestionEntity> questionOpt = questionService.getQuestion(questionId);
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            buttons.add(buttonUtils.createButton("Да", "DEL_QUESTION_CONFIRM " + questionId + " да"));
            buttons.add(buttonUtils.createButton("Нет", "DEL_QUESTION_CONFIRM " + questionId + " нет"));
            return new BotResponse(questionOpt.map(question -> {
                contextService.setCurrentQuestion(userId, question);
                stateService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
                return String.format("Вопрос “%s” будет удалён, вы уверены?", question.getQuestion());
            }).orElse("Вопрос не найден!"), buttons, false);
        }
        if (parts.length == 1) {
            stateService.changeStateById(userId, UserState.DELETE_QUESTION);
            return new BotResponse("Введите id вопроса для удаления:\n");
        }
        return new BotResponse("Ошибка ввода. Укажите корректный id теста.");
    }
}
