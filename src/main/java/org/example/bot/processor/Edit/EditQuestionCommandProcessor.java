package org.example.bot.processor.Edit;

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
 * Обработчик команды редактирования вопрса
 */
@Component
public class EditQuestionCommandProcessor extends AbstractCommandProcessor {

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
     * Утилита с вспомогательными методами
     */
    private final NumberUtils numberUtils;
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика команды редактирования вопроса
     *
     * @param stateService    cервис для управления состояниями
     * @param contextService  cервис для управления контекстом
     * @param questionService cервис для управления вопросами
     * @param numberUtils утилита с вспомогательными методами
     */
    public EditQuestionCommandProcessor(StateService stateService,
                                        ContextService contextService,
                                        QuestionService questionService,
                                        NumberUtils numberUtils, ButtonUtils buttonUtils) {
        super("/edit_question");
        this.stateService = stateService;
        this.contextService = contextService;
        this.questionService = questionService;
        this.numberUtils = numberUtils;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return new BotResponse("Используйте команду вместе с идентификатором вопроса!");
        }
        if (!numberUtils.isNumber(parts[1])) {
            return new BotResponse("Ошибка ввода. Укажите корректный id теста.");
        }

        Long questionId = Long.parseLong(parts[1]);
        Optional<QuestionEntity> questionOpt = questionService.getQuestion(questionId);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(buttonUtils.createButton("Формулировку вопроса", "EDIT_QUESTION " + questionId + " 1"));
        buttons.add(buttonUtils.createButton("Варианты ответа", "EDIT_QUESTION " + questionId + " 2"));
        return new BotResponse(questionOpt.map(question -> {
            contextService.setCurrentQuestion(userId, question);
            stateService.changeStateById(userId, UserState.EDIT_QUESTION);
            return String.format("""
                Вы выбрали вопрос “%s”. Что вы хотите изменить в вопросе?
                """, question.getQuestion());
        }).orElse("Вопрос не найден!"), buttons, false);
    }
}
