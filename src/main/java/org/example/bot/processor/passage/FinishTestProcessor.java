package org.example.bot.processor.passage;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCallbackProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработка завершения теста
 */
@Component
public class FinishTestProcessor extends AbstractCallbackProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    public FinishTestProcessor(StateService stateService, ContextService contextService, TestService testService) {
        super("FINISH_TEST");
        this.stateService = stateService;
        this.contextService = contextService;
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Optional<UserState> currentStateOpt = stateService.getCurrentState(userId);
        if (currentStateOpt.isEmpty() || !currentStateOpt.get().equals(UserState.PASSAGE_TEST)) {
            return new BotResponse("");
        }
        Optional<TestEntity> testOpt = contextService.getCurrentTest(userId);
        stateService.changeStateById(userId, UserState.DEFAULT);
        contextService.setCurrentTest(userId, null);
        contextService.setCurrentQuestion(userId, null);
        Optional<Integer> correctAnswerCountOptional = contextService.getCorrectAnswerCount(userId);
        Optional<Integer> countAnsweredQuestionsOptional = contextService.getCountAnsweredQuestions(userId);

        if (correctAnswerCountOptional.isEmpty() || countAnsweredQuestionsOptional.isEmpty() || testOpt.isEmpty()) {
            return new BotResponse("Проходимый тест не найден");
        }

        Integer correctAnswerCount = correctAnswerCountOptional.get();
        Integer countAnsweredQuestions = countAnsweredQuestionsOptional.get();
        Integer correctAnswerPercent = countAnsweredQuestions != 0
                ? (correctAnswerCount * 100) / countAnsweredQuestions
                : 0;

        TestEntity test = testOpt.get();
        test.setCountTries(test.getCountTries() + 1);
        test.setCorrectAnswerCountAllUsers(test.getCorrectAnswerCountAllUsers() + correctAnswerCount);
        test.setCountAnsweredQuestionsAllUsers(test.getCountAnsweredQuestionsAllUsers() + countAnsweredQuestions);
        testService.update(test);

        String text = "Тест завершен!\n" +
                String.format("Правильных ответов: %d/%d\n", correctAnswerCount, countAnsweredQuestions) +
                String.format("Процент правильных ответов: %d%%", correctAnswerPercent);

        return new BotResponse(text);
    }
}
