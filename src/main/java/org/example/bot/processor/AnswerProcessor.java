package org.example.bot.processor;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.TestEntity;
import org.example.bot.service.ContextService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработка ответа на вопрос
 */
@Component
public class AnswerProcessor extends AbstractCallbackProcessor {
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
    protected AnswerProcessor(ContextService contextService, ButtonUtils buttonUtils) {
        super("ANSWER_QUESTION");
        this.contextService = contextService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String callback) {
        String answer = extractData(callback);
        Optional<TestEntity> test = contextService.getCurrentTest(userId);
        Optional<QuestionEntity> previousQuestionOpt = contextService.getCurrentQuestion(userId);
        if(test.isEmpty() || previousQuestionOpt.isEmpty())
            return new BotResponse("Произошла ошибка при прохождении теста", null, false);

        QuestionEntity previousQuestion = previousQuestionOpt.get();
        List<QuestionEntity> questions = test.get().getQuestions();
        int nextQuestionIndex = questions.indexOf(previousQuestion) + 1;
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        previousQuestion.getAnswers()
                .forEach(answerEntity -> {
                    String postfix = "";
                    if (answerEntity.getAnswerText().equals(answer))
                        postfix = answerEntity.isCorrect()
                                ? " ✅"
                                : " ❌";
                    buttons.add(buttonUtils.createButton(answerEntity.getAnswerText() + postfix, "IGNORE"));
                });

        if(nextQuestionIndex < questions.size()) {
            buttons.add(buttonUtils.createButton("След. Вопрос", "EDIT_TEST_NEXT"));
        }
        else {
            buttons.add(buttonUtils.createButton("Завершить", "END_TEST"));
        }
        return new BotResponse("Вопрос %s/%s: %s"
                .formatted(nextQuestionIndex, questions.size(), previousQuestion.getQuestion())
                , buttons,
                true);
    }
}
