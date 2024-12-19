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
 * Обработать переход на следующий вопрос
 */
@Component
public class NextQuestionProcessor extends AbstractCallbackProcessor {
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
    protected NextQuestionProcessor(ContextService contextService, ButtonUtils buttonUtils) {
        super("NEXT_QUESTION");
        this.contextService = contextService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String callback) {
        Optional<TestEntity> test = contextService.getCurrentTest(userId);
        Optional<QuestionEntity> previousQuestionOpt = contextService.getCurrentQuestion(userId);
        if(test.isEmpty() || previousQuestionOpt.isEmpty())
            return new BotResponse("Произошла ошибка при прохождении теста", null, false);
        List<QuestionEntity> questions = test.get().getQuestions();
        QuestionEntity previousQuestion = previousQuestionOpt.get();
        int currentQuestionIndex = questions.indexOf(previousQuestion)+1;
        QuestionEntity currentQuestion = questions.get(currentQuestionIndex);

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        previousQuestion.getAnswers()
                .forEach(answerEntity -> buttons.add(buttonUtils.createButton(answerEntity.getAnswerText()
                        , "ANSWER_QUESTION " + answerEntity.getAnswerText())));

        if(currentQuestionIndex + 1 < questions.size()) {
            buttons.add(buttonUtils.createButton("След. Вопрос"
                    , "NEXT_QUESTION"));
        }
        else {
            buttons.add(buttonUtils.createButton("Завершить"
                    , "END_TEST"));
        }
        contextService.setCurrentQuestion(userId, currentQuestion);
        return new BotResponse("Вопрос %s/%s: %s"
                .formatted(currentQuestionIndex + 1, questions.size(), currentQuestion.getQuestion())
                ,buttons
                ,true);
    }
}
