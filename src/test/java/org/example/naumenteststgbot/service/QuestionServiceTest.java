package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.AnswerRepository;
import org.example.naumenteststgbot.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тесты сервиса для управления вопросами
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    /**
     * Репозиторий для работы с вопросом в базе данных
     */
    @Mock
    private QuestionRepository questionRepository;

    /**
     * Сервис для взаимодействия с сущностью теста
     */
    @Mock
    private TestService testService;

    /**
     * Сервис для взаимодействия с сущностью пользователя
     */
    @Mock
    private UserService userService;

    /**
     * Сервис для взаимодействия с сущностью вопроса
     */
    @InjectMocks
    private QuestionService questionService;


    /**
     * Тест на добавления нового вопроса к тесту с указанным id
     */
    @Test
    void testHandleAddQuestionWithTestId() {
        Long userId = 1L;
        String message = "/add_question 1";
        String[] parts = message.split(" ");
        Long testId = Long.parseLong(parts[1]);

        UserService userService = mock(UserService.class);
        TestService testService = mock(TestService.class);
        QuestionRepository questionRepository = mock(QuestionRepository.class);
        AnswerRepository answerRepository = mock(AnswerRepository.class);

        QuestionService questionService = new QuestionService(questionRepository, userService, answerRepository, testService);

        TestEntity test = mock(TestEntity.class);
        when(userService.getTestsById(userId)).thenReturn(List.of(test));

        when(testService.getTest(testId)).thenReturn(test);
        when(test.getTitle()).thenReturn("Test Title");

        String response = questionService.handleAddQuestion(userId, message);
        assertEquals("Введите название вопроса для теста “Test Title”", response);
        verify(userService).setState(eq(userId), eq(UserState.ADD_QUESTION_TEXT));
        verify(userService).setCurrentQuestion(eq(userId), any(QuestionEntity.class));
    }

    /**
     * Тест на добавления нового вопроса без указания id
     */
    @Test
    void testHandleAddQuestionWithoutTestId() {
        Long userId = 1L;
        String message = "/add_question";
        TestEntity test1 = mock(TestEntity.class);
        TestEntity test2 = mock(TestEntity.class);
        when(test1.getTitle()).thenReturn("Test 1");
        when(test2.getTitle()).thenReturn("Test 2");
        when(userService.getTestsById(userId)).thenReturn(List.of(test1, test2));

        String response = questionService.handleAddQuestion(userId, message);
        assertEquals("Выберите тест:\n1) id: 0 Название: Test 1\n2) id: 0 Название: Test 2\n", response);
    }

    /**
     * Тест на обработку команды добавления вопроса, если у пользователя нет доступных тестов
     */
    @Test
    void testHandleAddQuestionWithoutTestIdNoTests() {
        Long userId = 1L;
        String message = "/add_question";
        when(userService.getTestsById(userId)).thenReturn(List.of());

        String response = questionService.handleAddQuestion(userId, message);

        assertEquals("У вас нет доступных тестов для добавления вопросов.", response);
    }

    /**
     * Тест на обработку команды добавления вопроса с некорректным id
     */
    @Test
    void testHandleAddQuestionInvalidTestIdFormat() {
        Long userId = 1L;
        String message = "/add_question f";
        when(userService.getTestsById(userId)).thenReturn(List.of());
        String response = questionService.handleAddQuestion(userId, message);
        assertEquals("Некорректный формат id теста. Пожалуйста, введите число.", response);
    }

    /**
     * Тест на добавлениe вопроса, если тест с указанным id не найден
     */
    @Test
    void testHandleAddQuestionTestNotFound() {
        Long userId = 1L;
        String message = "/add_question 1";
        String[] parts = message.split(" ");
        Long testId = Long.parseLong(parts[1]);
        List<TestEntity> tests = List.of();
        when(userService.getTestsById(userId)).thenReturn(tests);
        when(testService.getTest(testId)).thenReturn(null);

        String response = questionService.handleAddQuestion(userId, message);
        assertEquals("Тест не найден!", response);
    }

    /**
     * Тест на обработку команды редактирования вопроса
     */
    @Test
    void testHandleEditQuestion() {
        Long userId = 1L;
        QuestionEntity question = mock(QuestionEntity.class);
        UserSession session = mock(UserSession.class);

        when(session.getState()).thenReturn(UserState.EDIT_QUESTION);
        when(session.getUserId()).thenReturn(userId);
        when(session.getCurrentQuestion()).thenReturn(question);

        String result = questionService.handleMessage(session, "1");

        assertEquals("Введите новый текст вопроса", result);
        verify(userService).setState(userId, UserState.EDIT_QUESTION_TEXT);
    }

    /**
     * Тест на обработку изменения текста вопроса
     */
    @Test
    void testHandleEditQuestionText() {
        Long userId = 1L;
        QuestionEntity question = mock(QuestionEntity.class);

        UserSession session = mock(UserSession.class);

        when(session.getState()).thenReturn(UserState.EDIT_QUESTION_TEXT);
        when(session.getUserId()).thenReturn(userId);
        when(session.getCurrentQuestion()).thenReturn(question);

        String result = questionService.handleMessage(session, "New question text");

        assertEquals("Текст вопроса изменен на “New question text”", result);
        verify(question).setQuestion("New question text");
        verify(questionRepository).save(question);
        verify(userService).setState(userId, UserState.DEFAULT);
    }

    /**
     * Тест на удаления вопроса
     */
    @Test
    void testHandleDeleteQuestion() {
        Long userId = 1L;
        QuestionEntity question = mock(QuestionEntity.class);
        UserSession session = mock(UserSession.class);

        when(session.getState()).thenReturn(UserState.DELETE_QUESTION);
        when(session.getUserId()).thenReturn(userId);
        when(session.getCurrentQuestion()).thenReturn(question);
        when(questionRepository.findById(10L)).thenReturn(java.util.Optional.of(question));

        String result = questionService.handleMessage(session, "10");

        assertEquals("Вопрос “null” будет удалён, вы уверены? (Да/Нет)", result);
        verify(userService).setCurrentQuestion(userId, question);
        verify(userService).setState(userId, UserState.CONFIRM_DELETE_QUESTION);
    }


    /**
     * Тест на возвращения ошибки, если тест не содержит вопросов
     */
    @Test
    void testHandleViewQuestionWhenTestNoQuestions() {
        Long userId = 1L;
        String message = "/viewQuestions 1";
        String[] parts = message.split(" ");
        Long testId = Long.parseLong(parts[1]);
        TestEntity test = mock(TestEntity.class);
        when(test.getCreatorId()).thenReturn(userId);
        when(test.getQuestions()).thenReturn(List.of());
        when(testService.getTest(testId)).thenReturn(test);

        String result = questionService.handleViewQuestions(userId, message);

        assertEquals("В этом тесте пока нет вопросов.", result);
        verify(testService).getTest(testId);
    }

    /**
     * Тест на возврат списка вопросов для валидного теста
     */
    @Test
    void testHandleViewQuestionsForValidTest() {
        Long userId = 1L;
        String message = "/view_questions 1";
        String[] parts = message.split(" ");
        Long testId = Long.parseLong(parts[1]);
        TestEntity test = mock(TestEntity.class);
        QuestionEntity question1 = mock(QuestionEntity.class);
        QuestionEntity question2 = mock(QuestionEntity.class);

        when(test.getCreatorId()).thenReturn(userId);
        when(test.getTitle()).thenReturn("Test");
        when(test.getQuestions()).thenReturn(List.of(question1, question2));
        when(testService.getTest(testId)).thenReturn(test);

        when(question1.getId()).thenReturn(1L);
        when(question1.getQuestion()).thenReturn("Question 1");
        when(question2.getId()).thenReturn(2L);
        when(question2.getQuestion()).thenReturn("Question 2");

        String result = questionService.handleViewQuestions(userId, message);

        assertEquals("""
                Вопросы теста "Test":
                1) id:1  "Question 1"
                2) id:2  "Question 2"
                """, result);
        verify(testService).getTest(testId);

    }

    /**
     * Тест на возвращения ошибки, если команда редактирования вопроса не содержит id
     */
    @Test
    void testHandleEditQuestionWhenCommandWithoutId() {
        Long userId = 1L;
        String message = "/edit_question";

        String result = questionService.handleEditQuestion(userId, message);

        assertEquals("Используйте команду вместе с идентификатором вопроса!", result);
        verifyNoInteractions(questionRepository, userService);
    }

    /**
     * Тест на обработу ситуации, когда вопрос не найден
     */
    @Test
    void testHandleEditQuestionWhenQuestionNotFound() {
        Long userId = 1L;
        String message = "/edit_question 1";
        String[] parts = message.split(" ");
        Long questionId = Long.parseLong(parts[1]);
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        String result = questionService.handleEditQuestion(userId, message);

        assertEquals("Вопрос не найден!", result);
        verify(questionRepository).findById(questionId);
        verifyNoMoreInteractions(questionRepository);
        verifyNoInteractions(userService);
    }

    /**
     * Тест на обработку команды редактирования вопроса с id
     */
    @Test
    void testHandleEditQuestionWithQuestionId() {
        Long userId = 1L;
        String message = "/edit_question 1";
        String[] parts = message.split(" ");
        Long questionId = Long.parseLong(parts[1]);
        QuestionEntity question = new QuestionEntity();
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        question.setQuestion("question");
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        String result = questionService.handleEditQuestion(userId, message);

        assertEquals("""
                Вы выбрали вопрос “question”. Что вы хотите изменить в вопросе?
                1: Формулировку вопроса
                2: Варианты ответа
                """, result);
        verify(questionRepository).findById(questionId);
        verify(userService).setCurrentQuestion(userId, question);
        verify(userService).setState(userId, UserState.EDIT_QUESTION);
    }

    /**
     * Тест на обработку команды удаления вопроса, если вопрос не найден
     */
    @Test
    void testHandleDeleteQuestionQuestionNotFound() {
        Long userId = 1L;
        UserSession session = mock(UserSession.class);
        when(userService.getSession(userId)).thenReturn(session);
        String message = "/del_question 1";
        String[] parts = message.split(" ");
        Long questionId = Long.parseLong(parts[1]);
        when(session.getState()).thenReturn(UserState.DELETE_QUESTION);
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        String response = questionService.handleDeleteQuestion(userId, message);

        assertEquals("Вопрос не найден!", response);
        verify(userService, never()).setCurrentQuestion(anyLong(), any());
    }

    /**
     * Тест на подтверждениe удаления вопроса
     */
    @Test
    void testHandleDeleteQuestionConfirmDelete() {
        Long userId = 1L;
        String message = "да";
        QuestionEntity question = mock(QuestionEntity.class);
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(UserState.CONFIRM_DELETE_QUESTION);
        when(session.getCurrentQuestion()).thenReturn(question);
        when(userService.getSession(userId)).thenReturn(session);

        String response = questionService.handleDeleteQuestion(userId, message);

        assertEquals("Вопрос успешно удален.", response);
        verify(questionRepository, times(1)).delete(question);
        verify(userService).setState(userId, UserState.DEFAULT);
    }

    /**
     * Тест на отмену удаления вопроса
     */
    @Test
    void testHandleDeleteQuestionCancelDelete() {
        Long userId = 1L;
        String message = "нет";
        QuestionEntity question = mock(QuestionEntity.class);
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(UserState.CONFIRM_DELETE_QUESTION);
        when(session.getCurrentQuestion()).thenReturn(question);
        when(userService.getSession(userId)).thenReturn(session);

        String response = questionService.handleDeleteQuestion(userId, message);

        assertEquals("Удаление вопроса отменено.", response);
        verify(questionRepository, never()).delete(any());
        verify(userService).setState(userId, UserState.DEFAULT);
    }

    /**
     * Тест на обработку команды остановки, если у пользователя нет текущего вопроса
     */
    @Test
    void testHandleStopNoCurrentQuestion() {
        Long userId = 1L;
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(UserState.ADD_ANSWER);
        when(userService.getSession(userId)).thenReturn(session);
        when(userService.getCurrentQuestion(userId)).thenReturn(null);

        String response = questionService.handleStop(userId);

        assertEquals("Нет текущего вопроса. Пожалуйста, выберите или создайте вопрос.", response);
    }

    /**
     * Тест на обработку команды остановки, если количество ответов недостаточно
     */
    @Test
    void testHandleStopNotEnoughAnswers() {
        Long userId = 1L;
        QuestionEntity question = mock(QuestionEntity.class);
        when(question.getAnswers()).thenReturn(List.of(new AnswerEntity("Answer 1")));
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(UserState.ADD_ANSWER);
        when(userService.getSession(userId)).thenReturn(session);
        when(userService.getCurrentQuestion(userId)).thenReturn(question);

        String response = questionService.handleStop(userId);

        assertEquals("Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.", response);
    }

    /**
     * Тест на обработку команды остановки и выбора правильного ответа
     */
    @Test
    void testHandleStopSetCorrectAnswer() {
        Long userId = 1L;
        QuestionEntity question = mock(QuestionEntity.class);
        when(question.getAnswers()).thenReturn(List.of(
                new AnswerEntity("Answer 1"),
                new AnswerEntity("Answer 2")
        ));
        UserSession session = mock(UserSession.class);
        when(session.getState()).thenReturn(UserState.ADD_ANSWER);
        when(userService.getSession(userId)).thenReturn(session);
        when(userService.getCurrentQuestion(userId)).thenReturn(question);

        String response = questionService.handleStop(userId);

        assertEquals("Укажите правильный вариант ответа:\n1: Answer 1\n2: Answer 2\n", response);
        verify(userService).setState(userId, UserState.SET_CORRECT_ANSWER);
    }
}