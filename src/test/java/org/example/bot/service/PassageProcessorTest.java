package org.example.bot.service;

import org.example.bot.entity.AnswerEntity;
import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.TestEntity;
import org.example.bot.processor.*;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.example.bot.util.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование прохождения теста
 * Проверяет начало теста, выбор правильного ответа, завершение и выход из теста
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PassageProcessorTest {

    /**
     * Сервис для управления контекстом
     */
    @Mock
    private ContextService contextService;

    /**
     * Сервис для управления состоянием
     */
    @Mock
    private StateService stateService;

    /**
     * Утилита для кнопок
     */
    private ButtonUtils buttonUtils = new ButtonUtils();

    /**
     * Утилита для тестов
     */
    private TestUtils testUtils = new TestUtils();

    /**
     * Обработчик начала теста
     */
    private StartTestProcessor startTestProcessor;

    /**
     * Обработчик ответов на вопросы
     */
    private AnswerProcessor answerProcessor;

    /**
     * Обработчик завершение теста
     */
    private FinishTestProcessor finishTestProcessor;

    /**
     * Обработчик выхода из теста
     */
    private ExitTestProcessor exitTestProcessor;

    /**
     * Идентификатор пользователя
     */
    private static final Long userId = 1L;

    /**
     * Тест
     */
    private static TestEntity testEntity;

    /**
     * Вопрос
     */
    private static QuestionEntity questionEntity;

    /**
     * Инициализация теста
     */
    @BeforeAll
    static void init() {
        testEntity = new TestEntity(userId, 12L);
        AnswerEntity answer1 = new AnswerEntity();
        answer1.setCorrect(true);
        answer1.setAnswerText("Ответ1");
        AnswerEntity answer2 = new AnswerEntity();
        answer2.setCorrect(false);
        answer2.setAnswerText("Ответ2");
        questionEntity = new QuestionEntity();
        questionEntity.getAnswers().add(answer1);
        questionEntity.getAnswers().add(answer2);
        questionEntity.setQuestion("Вопрос");
        testEntity.getQuestions().add(questionEntity);
    }

    /**
     * Инициализация для обработчиков
     */
    @BeforeEach
    void setUpProcessors() {
        startTestProcessor = new StartTestProcessor(contextService, stateService, buttonUtils, testUtils);
        answerProcessor = new AnswerProcessor(contextService, stateService, buttonUtils, testUtils);
        finishTestProcessor = new FinishTestProcessor(stateService, contextService);
        exitTestProcessor = new ExitTestProcessor(stateService, contextService);
    }

    /**
     * Очистка моков после каждого теста.
     */
    @AfterEach
    void clearMocks() {
        clearInvocations(contextService, stateService);
    }

    /**
     * Тестирует начало прохождения теста
     */
    @Test
    void shouldStartTest() {
        Optional<TestEntity> test = Optional.of(testEntity);
        when(contextService.getCurrentTest(userId)).thenReturn(test);

        BotResponse response = startTestProcessor.process(userId, "");

        verify(contextService, times(1)).getCurrentTest(userId);

        assertEquals("Вопрос 1/1: Вопрос\n" +
                "Варианты ответа:\n" +
                "1: Ответ1\n" +
                "2: Ответ2\n" +
                "\n" +
                "Выберите один вариант ответа:", response.getMessage());
        verify(contextService).setCurrentQuestion(userId, testEntity.getQuestions().getFirst());
        verify(stateService).changeStateById(userId, UserState.PASSAGE_TEST);
    }

    /**
     * Тестирует выбор правильного ответа
     */
    @Test
    void shouldAnswerQuestion() {
        when(stateService.getCurrentState(userId)).thenReturn(Optional.of(UserState.PASSAGE_TEST));
        when(contextService.getCurrentTest(userId)).thenReturn(Optional.of(testEntity));
        when(contextService.getCurrentQuestion(userId)).thenReturn(Optional.of(questionEntity));

        BotResponse response = answerProcessor.process(userId, "ANSWER_QUESTION Ответ1");

        assertNotNull(response);
        verify(contextService).incrementCorrectAnswerCount(userId);
    }

    /**
     * Тестирует завершение теста
     */
    @Test
    void shouldFinishTest() {
        Long userId = 1L;

        when(stateService.getCurrentState(userId)).thenReturn(Optional.of(UserState.PASSAGE_TEST));
        when(contextService.getCorrectAnswerCount(userId)).thenReturn(Optional.of(3));
        when(contextService.getCountAnsweredQuestions(userId)).thenReturn(Optional.of(5));

        BotResponse response = finishTestProcessor.process(userId, "");

        assertTrue(response.getMessage().contains("Тест завершен"));
        assertTrue(response.getMessage().contains("Процент правильных ответов: 60%"));
        verify(stateService).changeStateById(userId, UserState.DEFAULT);
    }

    /**
     * Тестирует выход из теста
     */
    @Test
    void shouldExitTest() {
        Long userId = 1L;

        when(stateService.getCurrentState(userId)).thenReturn(Optional.of(UserState.PASSAGE_TEST));

        BotResponse response = exitTestProcessor.process(userId, "");

        assertEquals("Вы вышли из теста", response.getMessage());
        verify(stateService).changeStateById(userId, UserState.DEFAULT);
        verify(contextService).setCurrentTest(userId, null);
    }
}
