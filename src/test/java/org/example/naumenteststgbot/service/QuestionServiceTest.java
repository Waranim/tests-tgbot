package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.AnswerRepository;
import org.example.naumenteststgbot.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
     * Создание сообщений
     */
    private MessageBuilder messageBuilder;

    /**
     * Сервис для создания inline клавиатур
     */
    @Mock
    private KeyboardService keyboardService;

    /**
     * Сервис для взаимодействия с сущностью вопроса
     */
    @InjectMocks
    private QuestionService questionService;

    private long userId;
    private long questionId;
    private long testId;
    private TestEntity test;
    private QuestionEntity question;
    private UserSession userSession;
    private AnswerEntity answer;
    private String chatId;


    /**
     * Инициализация перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        chatId = "1234567";
        userId = 1L;
        testId = 1L;
        questionId = 1L;
        messageBuilder = new MessageBuilder();
        questionService = new QuestionService(questionRepository, userService, answerRepository, testService, keyboardService, messageBuilder);
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
        SendMessage addAnswerMessage = questionService.handleMessage(chatId,userSession, answerText);
        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду “/stop”.", addAnswerMessage.getText());
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
     * Настраивает моки и процесс добавления вопроса
     */
    private void setupMocksAddQuestion() {
        setupMocksForAddQuestion();
        questionService.handleAddQuestion(userId, "/add_question 1");
        verify(userService).setState(userId, UserState.ADD_QUESTION_TEXT);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        questionService.handleMessage(chatId, userSession, "Вопрос");


        verify(userService).setState(userId, UserState.ADD_ANSWER);
        userSession.setState(UserState.ADD_ANSWER);
        questionService.handleStop(userId);

        addAnswerAndVerify("1", 1);
        addAnswerAndVerify("2", 2);

        questionService.handleStop(userId);
        verify(answerRepository, times(2)).save(any(AnswerEntity.class));
        verify(userService).setState(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        questionService.handleMessage(chatId,userSession, "2");
    }

    /**
     * Тестирует обработку команды добавления вопроса с указанием Id теста
     */
    @Test
    void testHandleAddQuestionWithTestId() {
        setupMocksForAddQuestion();
        String result = questionService.handleAddQuestion(userId, "/add_question 1");
        assertEquals("Введите название вопроса для теста “Test”", result);
        verify(userService).setState(userId, UserState.ADD_QUESTION_TEXT);

        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        SendMessage addTextMessage = questionService.handleMessage(chatId, userSession, "Вопрос");

        assertEquals("Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.", addTextMessage.getText());

        verify(userService).setState(userId, UserState.ADD_ANSWER);
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
        verify(userService).setState(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        SendMessage correctAnswer = questionService.handleMessage(chatId, userSession, "2");
        assertEquals("Вариант ответа 2 назначен правильным.", correctAnswer.getText());
        verify(userService, times(1)).setState(userId, UserState.DEFAULT);
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

        verify(userService).setState(userId, UserState.ADD_QUESTION);
        userSession.setState(UserState.ADD_QUESTION);

        SendMessage selectTestMessage = questionService.handleMessage(chatId,userSession, "1");
        assertEquals("Введите название вопроса для теста “Test”", selectTestMessage.getText());

        verify(userService).setState(userId, UserState.ADD_QUESTION_TEXT);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.ADD_QUESTION_TEXT);

        questionService.handleMessage(chatId, userSession, "Вопрос");


        verify(userService).setState(userId, UserState.ADD_ANSWER);
        userSession.setState(UserState.ADD_ANSWER);
        questionService.handleStop(userId);

        addAnswerAndVerify("1", 1);
        addAnswerAndVerify("2", 2);

        questionService.handleStop(userId);
        verify(answerRepository, times(2)).save(any(AnswerEntity.class));
        verify(userService).setState(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        questionService.handleMessage(chatId, userSession, "2");
        verify(userService, times(1)).setState(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует обработку команды добавления вопроса с некорректным id вопроса
     */
    @Test
    void testHandleAddQuestionInvalidId() {
        String result = questionService.handleAddQuestion(userId, "/add_question abc");
        assertEquals("Некорректный формат id теста. Пожалуйста, введите число.", result);
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

        String result = questionService.handleEditQuestion(chatId, userId, "/edit_question 1").getText();

        assertEquals("Что вы хотите изменить в вопросе “Вопрос” " , result);

        verify(userService).setState(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        verify(keyboardService).createReply(
                eq(List.of("Формулировку вопроса", "Варианты ответа")),
                eq(List.of("changeText 1", "changeAnswer 1")),
                eq("QUESTION")
        );
        SendMessage editTextMessage = questionService.handleMessage(chatId, userSession, "1");
        assertEquals("Введите новый текст вопроса", editTextMessage.getText());

        verify(userService).setState(userId, UserState.EDIT_QUESTION_TEXT);
        userSession.setState(UserState.EDIT_QUESTION_TEXT);

        SendMessage confirmEdit = questionService.handleMessage(chatId, userSession, "Другой вопрос");
        assertEquals("Текст вопроса изменен на “Другой вопрос”", confirmEdit.getText());

        verify(userService, times(2)).setState(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }


    /**
     * Тестирует редактирование текста варианта ответа
     */
    @Test
    void testHandleEditQuestionAnswerText() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        String result = questionService.handleEditQuestion(chatId, userId, "/edit_question 1").getText();
        assertEquals("Что вы хотите изменить в вопросе “Вопрос” ", result);

        verify(userService).setState(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        verify(keyboardService).createReply(
                eq(List.of("Формулировку вопроса", "Варианты ответа")),
                eq(List.of("changeText 1", "changeAnswer 1")),
                eq("QUESTION")
        );

        userSession.setEditingAnswerIndex(0);

        SendMessage editTextMessage = questionService.handleMessage(chatId, userSession, "2");
        assertEquals("Что вы хотите сделать с вариантом ответа?\n" +
                "1: Изменить формулировку ответа\n" +
                "2: Изменить правильность варианта ответа", editTextMessage.getText());

        verify(userService).setState(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_OPTION_CHOICE);

        SendMessage callbackChangeAnswer = questionService.handleCallback(chatId,"QUESTION changeAnswer 1",userId);
        assertEquals("Что вы хотите сделать?",callbackChangeAnswer.getText());
        verify(keyboardService).createReply(
                eq(List.of("Изменить формулировку ответа", "Правильность варианта ответа")),
                eq(List.of("changeTextAnswerOption 1", "changeCorrectAnswerOption 1")),
                eq("QUESTION")
        );


        SendMessage editAnswerText = questionService.handleMessage(chatId, userSession, "1");
        assertEquals("Сейчас варианты ответа выглядят так\n" +
                "1: 1\n" +
                "2: 2 (верный)\n" +
                "\n" +
                "Какой вариант ответа вы хотите изменить?", editAnswerText.getText());


        SendMessage callbackChangeTextAnswerOption = questionService.handleCallback(chatId,"QUESTION changeTextAnswerOption 1",userId);
        assertEquals("Какой вариант ответа вы хотите изменить?\n",callbackChangeTextAnswerOption.getText());
        verify(keyboardService).createReply(
                eq(List.of("1","2 (верный)")),
                eq(List.of("changeTextAnswer 1 0","changeTextAnswer 1 1")),
                eq("QUESTION")
        );

        verify(userService).setState(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_TEXT_CHOICE);

        SendMessage invalidIndex = questionService.handleMessage(chatId, userSession, "4");
        assertEquals("Некорректный номер ответа. Попробуйте еще раз.", invalidIndex.getText());

        SendMessage correctIndex = questionService.handleMessage(chatId, userSession, "1");
        assertEquals("Введите новую формулировку ответа", correctIndex.getText());

        verify(userService).setState(userId, UserState.EDIT_ANSWER_TEXT);
        userSession.setState(UserState.EDIT_ANSWER_TEXT);

        SendMessage newAnswerText = questionService.handleMessage(chatId, userSession, "Новая формулировка");
        assertEquals("Формулировка изменена на “Новая формулировка”", newAnswerText.getText());

        verify(userService, times(2)).setState(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует изменение правильного ответа
     */
    @Test
    void testHandleEditQuestionCorrectAnswer() {
        setupMocksAddQuestion();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        String result = questionService.handleEditQuestion(chatId, userId, "/edit_question 1").getText();
        assertEquals("Что вы хотите изменить в вопросе “Вопрос” ", result);

        verify(userService).setState(userId, UserState.EDIT_QUESTION);
        userSession.setCurrentQuestion(question);
        userSession.setState(UserState.EDIT_QUESTION);

        verify(keyboardService).createReply(
                eq(List.of("Формулировку вопроса", "Варианты ответа")),
                eq(List.of("changeText 1", "changeAnswer 1")),
                eq("QUESTION")
        );

        userSession.setEditingAnswerIndex(0);

        SendMessage editTextMessage = questionService.handleMessage(chatId, userSession, "2");
        assertEquals("Что вы хотите сделать с вариантом ответа?\n" +
                "1: Изменить формулировку ответа\n" +
                "2: Изменить правильность варианта ответа", editTextMessage.getText());

        verify(userService).setState(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
        userSession.setState(UserState.EDIT_ANSWER_OPTION_CHOICE);

        SendMessage callbackChangeAnswer = questionService.handleCallback(chatId,"QUESTION changeAnswer 1",userId);
        assertEquals("Что вы хотите сделать?",callbackChangeAnswer.getText());
        verify(keyboardService).createReply(
                eq(List.of("Изменить формулировку ответа", "Правильность варианта ответа")),
                eq(List.of("changeTextAnswerOption 1", "changeCorrectAnswerOption 1")),
                eq("QUESTION")
        );


        SendMessage editAnswerText = questionService.handleMessage(chatId, userSession, "2");
        assertEquals("Сейчас варианты ответа выглядят так:\n" +
                "1: 1\n" +
                "2: 2 (верный)\n" +
                "\n" +
                "Какой вариант ответа вы хотите сделать правильным?", editAnswerText.getText());


        SendMessage callbackChangeCorrectAnswerOption = questionService.handleCallback(chatId,"QUESTION changeCorrectAnswerOption 1",userId);
        assertEquals("Какой вариант ответа вы хотите изменить?\n",callbackChangeCorrectAnswerOption.getText());
        verify(keyboardService).createReply(
                eq(List.of("1","2 (верный)")),
                eq(List.of("changeCorrectAnswer 1 0","changeCorrectAnswer 1 1")),
                eq("QUESTION")
        );

        verify(userService, times(2)).setState(userId, UserState.SET_CORRECT_ANSWER);
        userSession.setState(UserState.SET_CORRECT_ANSWER);

        String invalidIndex = questionService.handleMessage(chatId, userSession, "4").getText();
        assertEquals("Некорректный номер варианта ответа. Введите число от 1 до 2", invalidIndex);

        String correctIndex = questionService.handleMessage(chatId, userSession, "1").getText();
        assertEquals("Вариант ответа 1 назначен правильным.", correctIndex);

        verify(userService, times(2)).setState(userId, UserState.DEFAULT);
        userSession.setState(UserState.DEFAULT);
    }

    /**
     * Тестирует обработку команды редактирования вопроса без указания id
     */
    @Test
    void testHandleEditQuestionWithoutQuestionId() {
        setupMocksAddQuestion();
        String result = questionService.handleEditQuestion(chatId, userId, "/edit_question").getText();
        assertEquals("Используйте команду вместе с идентификатором вопроса!", result);
    }

    /**
     * Тестирует обработку команды редактирования вопроса с отсутствующим вопросом
     */
    @Test
    void testHandleEditQuestionQuestionNotFound() {
        String result = questionService.handleEditQuestion(chatId, userId, "/edit_question 2").getText();
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

        String invalidId = questionService.handleDeleteQuestion(chatId, userId, "/del_question asda").getText();
        assertEquals("Некорректный формат id вопроса. Пожалуйста, введите число.", invalidId);

        String result = questionService.handleDeleteQuestion(chatId, userId, "/del_question 1").getText();
        assertEquals("Вы уверены, что хотите удалить вопрос “Вопрос”?", result);

        verify(keyboardService).createReply(
                eq(List.of("Да", "Нет")),
                eq(List.of("confirmDeleteYes", "confirmDeleteNo")),
                eq("QUESTION")
        );

        verify(userService).setState(userId, UserState.CONFIRM_DELETE_QUESTION);
        userSession.setState(UserState.CONFIRM_DELETE_QUESTION);

        String confirmDelete = questionService.handleMessage(chatId, userSession, "Да").getText();
        assertEquals("Вопрос “Вопрос” из теста “Test” удален.", confirmDelete);

        String notСonfirmDelete = questionService.handleMessage(chatId, userSession, "Нет").getText();
        assertEquals("Вопрос “Вопрос” из теста “Test” не удален.", notСonfirmDelete);

        verify(userService, times(3)).setState(userId, UserState.DEFAULT);
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

        String invalidId = questionService.handleDeleteQuestion(chatId, userId, "/del_question asda").getText();
        assertEquals("Некорректный формат id вопроса. Пожалуйста, введите число.", invalidId);

        String result = questionService.handleDeleteQuestion(chatId, userId, "/del_question").getText();
        assertEquals("Введите id вопроса для удаления:\n", result);

        verify(userService).setState(userId, UserState.DELETE_QUESTION);
        userSession.setState(UserState.DELETE_QUESTION);

        String questionId = questionService.handleMessage(chatId, userSession, "1").getText();
        assertEquals("Вы уверены, что хотите удалить вопрос “Вопрос”?", questionId);

        verify(keyboardService).createReply(
                eq(List.of("Да", "Нет")),
                eq(List.of("confirmDeleteYes", "confirmDeleteNo")),
                eq("QUESTION")
        );

        verify(userService).setState(userId, UserState.CONFIRM_DELETE_QUESTION);
        userSession.setState(UserState.CONFIRM_DELETE_QUESTION);

        String confirmDelete = questionService.handleMessage(chatId, userSession, "Да").getText();
        assertEquals("Вопрос “Вопрос” из теста “Test” удален.", confirmDelete);

        String notСonfirmDelete = questionService.handleMessage(chatId, userSession, "Нет").getText();
        assertEquals("Вопрос “Вопрос” из теста “Test” не удален.", notСonfirmDelete);

        verify(userService, times(3)).setState(userId, UserState.DEFAULT);
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