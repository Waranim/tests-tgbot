package org.example.bot.processor.Edit;

import org.example.bot.entity.AnswerEntity;
import org.example.bot.entity.QuestionEntity;
import org.example.bot.processor.AbstractCallbackProcessor;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.AnswerUtils;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Oбработчик состояния редактирования ответа
 */
@Component
public class EditAnswerOptionChoiceProcessor extends AbstractCallbackProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;


    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика редактирования ответа
     *
     * @param stateService   сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     */
    public EditAnswerOptionChoiceProcessor(StateService stateService,
                                           ContextService contextService,
                                           ButtonUtils buttonUtils) {
        super("EDIT_ANSWER_OPTION_CHOICE");
        this.stateService = stateService;
        this.contextService = contextService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        String[] parts = message.split(" ");
        Optional<QuestionEntity> optionalCurrentQuestion = contextService.getCurrentQuestion(userId);
        if (optionalCurrentQuestion.isEmpty()) {
            return new BotResponse("Вопрос не найден");
        }
        QuestionEntity currentQuestion = optionalCurrentQuestion.get();
        List<AnswerEntity> answers = currentQuestion.getAnswers();
        List<InlineKeyboardButton> button = new ArrayList<>();
        if (currentQuestion.getId() == Integer.parseInt(parts[1])) {
            if (parts[2].equals("1")) {
                stateService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
                for (int i = 0; i < answers.size(); i++) {
                    AnswerEntity answer = answers.get(i);
                    String text = answer.isCorrect() ? answer.getAnswerText() + " (верный)" : answer.getAnswerText();
                    button.add(buttonUtils.createButton(text, "EDIT_ANSWER_TEXT_CHOICE " + i));
                }
                return new BotResponse("Какой вариант ответа вы хотите изменить?",
                        button, false);

            } else if (parts[2].equals("2")) {
                stateService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
                for (int i = 0; i < answers.size(); i++) {
                    AnswerEntity answer = answers.get(i);
                    String text = answer.isCorrect() ? answer.getAnswerText() + " (верный)" : answer.getAnswerText();
                    button.add(buttonUtils.createButton(text, "SET_CORRECT_ANSWER " + i));
                }
                return new BotResponse("Какой вариант ответа вы хотите сделать правильным?",
                        button, false);
            }
        }
        return new BotResponse("Некорректный ввод");
    }
}
