package org.example.bot.processor;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.TestEntity;
import org.example.bot.service.ContextService;
import org.example.bot.service.UserService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработать начало теста
 */
@Component
public class StartTestProcessor extends AbstractCallbackProcessor {

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;
    /**
     * Утилита для кнопок
     */
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    protected StartTestProcessor(ContextService contextService, ButtonUtils buttonUtils) {
        super("START_TEST");
        this.contextService = contextService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Optional<TestEntity> testOpt = contextService.getCurrentTest(userId);
        if(testOpt.isEmpty())
            return new BotResponse("Тест не найден");

        List<QuestionEntity> questions = testOpt.get().getQuestions();
        if (questions.isEmpty()) {
            return new BotResponse("Вопросы в тесте отсутствуют.");
        }

        contextService.setCurrentQuestion(userId, questions.getFirst());
        return createQuestionMessage(questions);
    }

    /**
     * Создать сообщение с вопросом
     */
    private BotResponse createQuestionMessage(List<QuestionEntity> questions) {
        QuestionEntity question = questions.getFirst();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        question.getAnswers()
                .forEach(a -> buttons.add(buttonUtils.createButton(a.getAnswerText()
                        , "ANSWER_QUESTION " + a.getAnswerText())));
        if (0 < questions.size() - 1) {
            buttons.add(buttonUtils.createButton("След. Вопрос", "NEXT_QUESTION 1"));
        } else {
            buttons.add(buttonUtils.createButton("Завершить", "END_TEST"));
        }

        return new BotResponse(String
                .format("Вопрос %d/%d: %s", 1, questions.size(), question.getQuestion()),
                buttons
                ,true);
    }
}
