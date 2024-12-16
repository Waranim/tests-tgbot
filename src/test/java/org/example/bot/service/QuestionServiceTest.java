package org.example.bot.service;

import org.example.bot.entity.*;
import org.example.bot.enums.UserState;
import org.example.bot.repository.AnswerRepository;
import org.example.bot.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
     * Репозиторий для взаимодействия над сущностью ответов в базе данных
     */
    @Mock
    private AnswerRepository answerRepository;

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
     * Идентификатор пользователя
     */
    private long userId;

    /**
     * Идентификатор вопроса
     */
    private long questionId;

    /**
     * Идентификатор теста
     */
    private long testId;

    /**
     * Сущность теста
     */
    private TestEntity test;

    /**
     * Сущность вопроса
     */
    private QuestionEntity question;

    /**
     * Текущая сессия пользователя
     */
    private UserSession userSession;

    /**
     * Сущность ответа
     */
    private AnswerEntity answer;

    /**
     * Инициализация перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        userId = 1L;
        testId = 1L;
        questionId = 1L;
        test = new TestEntity(userId);
        question = new QuestionEntity();
        test.setTitle("Test");
        ReflectionTestUtils.setField(test, "id", testId);
        ReflectionTestUtils.setField(question, "id", questionId);
        userSession = new UserSession(userId);
        answer = new AnswerEntity();
    }

    /**
     * Добавляет ответ и проверяет его сохранение
     *
     * @param answerText текст ответа
     * @param timesSaved количество раз, когда ответ должен быть сохранен
     */
    private void addAnswerAndVerify(String answerText, int timesSaved) {
        String addAnswerMessage = questionService.handleMessage(userSession, answerText);
        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду “/stop”.", addAnswerMessage);
        verify(answerRepository, times(timesSaved)).save(any(AnswerEntity.class));
    }

    /**
     * Настраивает моки для добавления вопроса
     */
    private void setupMocksForAddQuestion() {
        when(userService.getSession(userId)).thenReturn(userSession);
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(userService.getCurrentQuestion(userId)).thenReturn(question);
        when(testService.getTest(testId)).thenReturn(test);
        when(questionRepository.save(any(QuestionEntity.class))).thenReturn(question);
        when(answerRepository.save(any(AnswerEntity.class))).thenReturn(answer);
    }

    /**
     * Настраивает моки и тестирует процесс добавления вопроса
     */
    private void setupMocksAddQuestion() {
        setupMocksForAddQuestion();
        String result = questionService.handleAddQuestion(userId, "/add_question 1");
        assertEquals("Введите название вопроса для теста “Test”", result);
        verify(userService).changeStateById(userId, UserState.ADD_QUESTION_TEXT);

        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        String addTextMessage = questionService.handleMessage(userSession, "Вопрос");
        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.", addTextMessage);

        verify(userService).changeStateById(userId, UserState.ADD_ANSWER);
        userSession.setState(UserState.ADD_ANSWER);
        String stop1 = questionService.handleStop(userId);
        assertEquals("Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.", stop1);

        addAnswerAndVerify("1", 1);
        addAnswerAndVerify("2", 2);

        String stop2 = questionService.handleStop(userId);
        assertEquals("Укажите правильный вариант ответа:\n" +
                "1: 1\n" +
                "2: 2\n", stop2);
        verify(answerRepository, times(2)).save(any(AnswerEntity.class));
        verify(userService).changeStateById(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        String correctAnswer = questionService.handleMessage(userSession, "2");
        assertEquals("Вариант ответа 2 назначен правильным.", correctAnswer);

    }

    /**
     * Тестирует обработку команды добавления вопроса с указанием Id теста
     */
    @Test
    void testHandleAddQuestionWithTestId() {
        setupMocksForAddQuestion();
        String result = questionService.handleAddQuestion(userId, "/add_question 1");
        assertEquals("Введите название вопроса для теста “Test”", result);
        verify(userService).changeStateById(userId, UserState.ADD_QUESTION_TEXT);

        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        String addTextMessage = questionService.handleMessage(userSession, "Вопрос");
        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.", addTextMessage);

        verify(userService).changeStateById(userId, UserState.ADD_ANSWER);
        userSession.setState(UserState.ADD_ANSWER);
        String stop1 = questionService.handleStop(userId);
        assertEquals("Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.", stop1);

        addAnswerAndVerify("1", 1);
        addAnswerAndVerify("2", 2);

        String stop2 = questionService.handleStop(userId);
        assertEquals("Укажите правильный вариант ответа:\n" +
                "1: 1\n" +
                "2: 2\n", stop2);
        verify(answerRepository, times(2)).save(any(AnswerEntity.class));
        verify(userService).changeStateById(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        String correctAnswer = questionService.handleMessage(userSession, "2");
        assertEquals("Вариант ответа 2 назначен правильным.", correctAnswer);
        verify(userService, times(1)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует обработку команды добавления вопроса без указания id вопроса
     */
    @Test
    void testHandleAddQuestionWithoutTestId() {
        setupMocksForAddQuestion();
        String result = questionService.handleAddQuestion(userId, "/add_question");
        assertEquals("Выберите тест:\n1) id: 1 Название: Test\n", result);

        verify(userService).changeStateById(userId, UserState.ADD_QUESTION);
        userSession.setState(UserState.ADD_QUESTION);

        String selectTestMessage = questionService.handleMessage(userSession, "1");
        assertEquals("Введите название вопроса для теста “Test”", selectTestMessage);

        verify(userService).changeStateById(userId, UserState.ADD_QUESTION_TEXT);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        String addTextMessage = questionService.handleMessage(userSession, "Вопрос");
        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.", addTextMessage);

        verify(userService).changeStateById(userId, UserState.ADD_ANSWER);
        userSession.setState(UserState.ADD_ANSWER);
        String stop1 = questionService.handleStop(userId);
        assertEquals("Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.", stop1);

        addAnswerAndVerify("1", 1);
        addAnswerAndVerify("2", 2);

        String stop2 = questionService.handleStop(userId);
        assertEquals("Укажите правильный вариант ответа:\n" +
                "1: 1\n" +
                "2: 2\n", stop2);

        verify(answerRepository, times(2)).save(any(AnswerEntity.class));
        verify(userService).changeStateById(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        String correctAnswer = questionService.handleMessage(userSession, "2");
        assertEquals("Вариант ответа 2 назначен правильным.", correctAnswer);
        verify(userService, times(1)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует обработку команды добавления вопроса с некорректным id вопроса
     */
    @Test
    void testHandleAddQuestionInvalidId() {
        String result = questionService.handleAddQuestion(userId, "/add_question abc");
        assertEquals("Ошибка ввода. Укажите корректный id теста.", result);
    }

    /**
     * Тестирует обработку команды добавления вопроса с отсутствующим тестом
     */
    @Test
    void testHandleAddQuestionNotFound() {
        String result = questionService.handleAddQuestion(userId, "/add_question 2");
        assertEquals("Тест не найден!", result);

    }

    /**
     * Тестирует редактирование текста вопроса
     */
    @Test
    void testHandleEditQuestionText() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        String result = questionService.handleEditQuestion(userId, "/edit_question 1");
        assertEquals("""
                Вы выбрали вопрос “Вопрос”. Что вы хотите изменить в вопросе?
                1: Формулировку вопроса
                2: Варианты ответа
                """, result);

        verify(userService).changeStateById(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        String editTextMessage = questionService.handleMessage(userSession, "1");
        assertEquals("Введите новый текст вопроса", editTextMessage);

        verify(userService).changeStateById(userId, UserState.EDIT_QUESTION_TEXT);
        userSession.setState(UserState.EDIT_QUESTION_TEXT);

        String confirmEdit = questionService.handleMessage(userSession, "Другой вопрос");
        assertEquals("Текст вопроса изменен на “Другой вопрос”", confirmEdit);

        verify(userService, times(2)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует редактирование текста варианта ответа
     */
    @Test
    void testHandleEditQuestionAnswerText() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        String result = questionService.handleEditQuestion(userId, "/edit_question 1");
        assertEquals("""
                Вы выбрали вопрос “Вопрос”. Что вы хотите изменить в вопросе?
                1: Формулировку вопроса
                2: Варианты ответа
                """, result);

        verify(userService).changeStateById(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        userSession.setEditingAnswerIndex(0);

        String editTextMessage = questionService.handleMessage(userSession, "2");
        assertEquals("Что вы хотите сделать с вариантом ответа?\n" +
                "1: Изменить формулировку ответа\n" +
                "2: Изменить правильность варианта ответа", editTextMessage);

        verify(userService).changeStateById(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_OPTION_CHOICE);

        String editAnswerText = questionService.handleMessage(userSession, "1");
        assertEquals("Сейчас варианты ответа выглядят так\n" +
                "1: 1\n" +
                "2: 2 (верный)\n" +
                "\n" +
                "Какой вариант ответа вы хотите изменить?", editAnswerText);
        verify(userService).changeStateById(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_TEXT_CHOICE);

        String invalidIndex = questionService.handleMessage(userSession, "4");
        assertEquals("Некорректный номер ответа. Попробуйте еще раз.", invalidIndex);

        String correctIndex = questionService.handleMessage(userSession, "1");
        assertEquals("Введите новую формулировку ответа", correctIndex);

        verify(userService).changeStateById(userId, UserState.EDIT_ANSWER_TEXT);
        userSession.setState(UserState.EDIT_ANSWER_TEXT);

        String newAnswerText = questionService.handleMessage(userSession, "Новая формулировка");
        assertEquals("Формулировка изменена на “Новая формулировка”", newAnswerText);

        verify(userService, times(2)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует изменение правильного ответа
     */
    @Test
    void testHandleEditQuestionCorrectAnswer() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        String result = questionService.handleEditQuestion(userId, "/edit_question 1");
        assertEquals("""
                Вы выбрали вопрос “Вопрос”. Что вы хотите изменить в вопросе?
                1: Формулировку вопроса
                2: Варианты ответа
                """, result);

        verify(userService).changeStateById(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        userSession.setEditingAnswerIndex(0);

        String editTextMessage = questionService.handleMessage(userSession, "2");
        assertEquals("Что вы хотите сделать с вариантом ответа?\n" +
                "1: Изменить формулировку ответа\n" +
                "2: Изменить правильность варианта ответа", editTextMessage);

        verify(userService).changeStateById(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_OPTION_CHOICE);

        String editCorrectAnswer = questionService.handleMessage(userSession, "2");
        assertEquals("Сейчас варианты ответа выглядят так:\n" +
                "1: 1\n" +
                "2: 2 (верный)\n" +
                "\n" +
                "Какой вариант ответа вы хотите сделать правильным?", editCorrectAnswer);
        verify(userService, times(2)).changeStateById(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        String invalidIndex = questionService.handleMessage(userSession, "4");
        assertEquals("Некорректный номер варианта ответа. Введите число от 1 до 2", invalidIndex);

        String correctIndex = questionService.handleMessage(userSession, "1");
        assertEquals("Вариант ответа 1 назначен правильным.", correctIndex);

        verify(userService, times(2)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует обработку команды редактирования вопроса без указания id
     */
    @Test
    void testHandleEditQuestionWithoutQuestionId() {
        setupMocksAddQuestion();
        String result = questionService.handleEditQuestion(userId, "/edit_question");
        assertEquals("Используйте команду вместе с идентификатором вопроса!", result);
    }

    /**
     * Тестирует обработку команды редактирования вопроса с отсутствующим вопросом
     */
    @Test
    void testHandleEditQuestionQuestionNotFound() {
        String result = questionService.handleEditQuestion(userId, "/edit_question 2");
        assertEquals("Вопрос не найден!", result);
    }

    /**
     * Тестирует удаление вопроса по id
     */
    @Test
    void testHandleDeleteQuestionWithQuestionId() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        userSession.setCurrentQuestion(question);
        question.setTest(test);

        String invalidId = questionService.handleDeleteQuestion(userId, "/del_question asda");
        assertEquals("Некорректный формат id вопроса. Пожалуйста, введите число.", invalidId);

        String result = questionService.handleDeleteQuestion(userId, "/del_question 1");
        assertEquals("Вопрос “Вопрос” будет удалён, вы уверены? (Да/Нет)", result);

        verify(userService).changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
        userSession.setState(UserState.CONFIRM_DELETE_QUESTION);

        String confirmDelete = questionService.handleMessage(userSession, "Да");
        assertEquals("Вопрос “Вопрос” из теста “Test” удален.", confirmDelete);

        String notСonfirmDelete = questionService.handleMessage(userSession, "Нет");
        assertEquals("Вопрос “Вопрос” из теста “Test” не удален.", notСonfirmDelete);

        verify(userService, times(3)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует удаление вопроса без указания id
     */
    @Test
    void testHandleDeleteQuestionWithoutQuestionId() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        userSession.setCurrentQuestion(question);
        question.setTest(test);

        String invalidId = questionService.handleDeleteQuestion(userId, "/del_question asda");
        assertEquals("Некорректный формат id вопроса. Пожалуйста, введите число.", invalidId);

        String result = questionService.handleDeleteQuestion(userId, "/del_question");
        assertEquals("Введите id вопроса для удаления:\n", result);

        verify(userService).changeStateById(userId, UserState.DELETE_QUESTION);
        userSession.setState(UserState.DELETE_QUESTION);

        String questionId = questionService.handleMessage(userSession, "1");
        assertEquals("Вопрос “Вопрос” будет удалён, вы уверены? (Да/Нет)", questionId);

        verify(userService).changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
        userSession.setState(UserState.CONFIRM_DELETE_QUESTION);

        String confirmDelete = questionService.handleMessage(userSession, "Да");
        assertEquals("Вопрос “Вопрос” из теста “Test” удален.", confirmDelete);

        String notСonfirmDelete = questionService.handleMessage(userSession, "Нет");
        assertEquals("Вопрос “Вопрос” из теста “Test” не удален.", notСonfirmDelete);

        verify(userService, times(3)).changeStateById(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует просмотр вопросов теста по id
     */
    @Test
    void testHandleViewQuestionWithTestId() {
        TestEntity test = mock(TestEntity.class);
        QuestionEntity question1 = mock(QuestionEntity.class);
        QuestionEntity question2 = mock(QuestionEntity.class);

        when(test.getCreatorId()).thenReturn(userId);
        when(test.getTitle()).thenReturn("Test");
        when(test.getQuestions()).thenReturn(List.of(question1, question2));
        when(testService.getTest(testId)).thenReturn(test);


        when(question1.getId()).thenReturn(1L);
        when(question1.getQuestion()).thenReturn("Вопрос 1");
        when(question2.getId()).thenReturn(2L);
        when(question2.getQuestion()).thenReturn("Вопрос 2");

        String result = questionService.handleViewQuestions(userId, "/view_question 1");
        assertEquals("Вопросы теста \"Test\":\n" +
                "1) id:1  \"Вопрос 1\"\n" +
                "2) id:2  \"Вопрос 2\"\n", result);
    }

    /**
     * Тестирует просмотр вопросов теста без указания идентификатора id
     */
    @Test
    void testHandleViewQuestionWithoutTestId() {
        String result = questionService.handleViewQuestions(userId, "/view_question");
        assertEquals("Используйте команду вместе с идентификатором вопроса!", result);
    }

    /**
     * Тестирует просмотр вопросов для теста, который не найден
     */
    @Test
    void testHandleViewQuestionNotFoundTest() {
        String result = questionService.handleViewQuestions(userId, "/view_question 2");
        assertEquals("Тест не найден!", result);
    }

    /**
     * Тестирует случай, когда у теста отсутствуют вопросы
     */
    @Test
    void testHandleViewQuestionNotFoundQuestion() {
        TestEntity test = mock(TestEntity.class);
        when(test.getCreatorId()).thenReturn(userId);
        when(test.getQuestions()).thenReturn(List.of());
        when(testService.getTest(testId)).thenReturn(test);
        String result = questionService.handleViewQuestions(userId, "/view_question 1");
        assertEquals("В этом тесте пока нет вопросов.", result);
    }
}