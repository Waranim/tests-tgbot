package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.AnswerEntity;
import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.enums.UserState;
import org.example.naumenteststgbot.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для TestService
 */
@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    /**
     * Репозиторий тестов
     */
    @Mock
    private TestRepository testRepository;

    /**
     * Сервис пользователей
     */
    @Mock
    private UserService userService;

    /**
     * Создатель сообщений
     */
    @Mock
    private MessageBuilder messageBuilder;

    /**
     * Сервис создания inline-клавиатуры
     */
    @Mock
    private KeyboardService keyboardService;

    /**
     * Сервис тестов
     */
    @InjectMocks
    private TestService testService;

    /**
     * Идентификатор пользователя
     */
    private long userId;

    /**
     * Идентификатор теста
     */
    private long testId;

    /**
     * Тест
     */
    private TestEntity test;

    /**
     * Сессия пользователя
     */
    private UserSession userSession;

    /**
     * Инициализация перед каждым тестом
     */
    @BeforeEach
    void setUp() {


        userId = 123L;
        testId = 123L;
        test = new TestEntity(userId);
        test.setTitle("Название теста");
        ReflectionTestUtils.setField(test, "id", testId);
        userSession = new UserSession(userId);

        AnswerEntity answer = new AnswerEntity();
        answer.setCorrect(true);
        answer.setAnswerText("Ответ");
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.getAnswers().add(answer);
        questionEntity.setQuestion("Вопрос");
        test.getQuestions().add(questionEntity);
    }

    /**
     * Тестирование команды add
     */
    @Test
    void testHandleAdd() {
        when(testRepository.save(any(TestEntity.class))).thenReturn(test);

        String addMessage = testService.handleAdd(userId);
        assertEquals("Введите название теста", addMessage);

        verify(userService).changeStateById(userId, UserState.ADD_TEST_TITLE);
        verify(userService).setCurrentTest(userId, test);
        userSession.setCurrentTest(test);
        userSession.setState(UserState.ADD_TEST_TITLE);

        String addTitleMessage = testService.handleMessage(userSession, "Название теста 2");
        assertEquals("Введите описание теста", addTitleMessage);

        verify(userService).changeStateById(userId, UserState.ADD_TEST_DESCRIPTION);
        userSession.setState(UserState.ADD_TEST_DESCRIPTION);

        String addDescriptionMessage = testService.handleMessage(userSession, "Описание теста 2");
        assertEquals("Тест “Название теста 2” создан! Количество вопросов: 0. Для добавление вопросов используйте /add_question 123, где 123 - идентификатор теста “Название теста 2”.", addDescriptionMessage);
    }

    /**
     * Тестирование команды /view, когда есть 1 тест у пользователя
     */
    @Test
    void testHandleView() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));

        String result = testService.handleView(userId, "/view");

        assertEquals("Выберите тест для просмотра:\n1)  id: 123 Название теста\n", result);
        verify(userService).changeStateById(userId, UserState.VIEW_TEST);
        userSession.setState(UserState.VIEW_TEST);

        result = testService.handleMessage(userSession, "123");
        assertEquals("""
                Тест “Название теста”. Всего вопросов: 1
                Вопрос: Вопрос
                Варианты ответов:
                1 - Ответ
                Правильный вариант: Ответ
                
                """, result);
    }

    /**
     * Тестирование команды /view [testID], когда этот тест есть у пользователя
     */
    @Test
    void testHandleViewWithId() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));
        String result = testService.handleView(userId, "/view 123");
        assertEquals("""
                Тест “Название теста”. Всего вопросов: 1
                Вопрос: Вопрос
                Варианты ответов:
                1 - Ответ
                Правильный вариант: Ответ
                
                """, result);
    }

    /**
     * Тестирование команды /view [testID], когда этого теста у пользователя нет
     */
    @Test
    void testHandleViewNotFound() {
        when(userService.getTestsById(userId)).thenReturn(List.of());

        String result = testService.handleView(userId, "/view 999");

        assertEquals("Тест не найден!", result);
        verify(userService).changeStateById(userId, UserState.DEFAULT);
    }

    /**
     * Тестирование команды /view [testID], когда id теста неправильный
     */
    @Test
    void testHandleViewInvalidId() {
        String result = testService.handleView(userId, "/view AAA");
        assertEquals("Ошибка ввода!", result);
    }

    /**
     * Тестирование команды /edit [testID] с изменением названия теста
     */
    @Test
    void testHandleEditTitle() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(testRepository.save(test)).thenReturn(test);
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));

        String result = testService.handleEdit(userId, "/edit 123");

        assertEquals("""
                Вы выбрали тест “Название теста”. Что вы хотите изменить?
                1: Название теста
                2: Описание теста
                """, result);
        verify(userService).changeStateById(userId, UserState.EDIT_TEST);
        verify(userService).setCurrentTest(userId, test);
        userSession.setCurrentTest(test);
        userSession.setState(UserState.EDIT_TEST);
        result = testService.handleMessage(userSession, "1");
        assertEquals("Введите новое название теста", result);

        verify(userService).changeStateById(userId, UserState.EDIT_TEST_TITLE);
        userSession.setState(UserState.EDIT_TEST_TITLE);
        result = testService.handleMessage(userSession, "Измененное название теста");
        assertEquals("Название изменено на “Измененное название теста”", result);
        verify(userService).changeStateById(userId, UserState.DEFAULT);
    }

    /**
     * Тестирование команды /edit [testID] с изменением описания теста
     */
    @Test
    void testHandleEditDescription() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(testRepository.save(test)).thenReturn(test);
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));

        String result = testService.handleEdit(userId, "/edit 123");

        assertEquals("""
                Вы выбрали тест “Название теста”. Что вы хотите изменить?
                1: Название теста
                2: Описание теста
                """, result);
        verify(userService).changeStateById(userId, UserState.EDIT_TEST);
        verify(userService).setCurrentTest(userId, test);
        userSession.setCurrentTest(test);
        userSession.setState(UserState.EDIT_TEST);
        result = testService.handleMessage(userSession, "2");
        assertEquals("Введите новое описание теста", result);

        verify(userService).changeStateById(userId, UserState.EDIT_TEST_DESCRIPTION);
        userSession.setState(UserState.EDIT_TEST_DESCRIPTION);
        result = testService.handleMessage(userSession, "Новое описание теста");
        assertEquals("Описание изменено на “Новое описание теста”", result);
        verify(userService).changeStateById(userId, UserState.DEFAULT);
    }

    /**
     * Тестирование команды /edit [testID] с тестом, которого нет у пользователя
     */
    @Test
    void testHandleEditNotFound() {
        when(userService.getTestsById(userId)).thenReturn(List.of());

        String result = testService.handleEdit(userId, "/edit 555");

        assertEquals("Тест не найден!", result);
    }

    /**
     * Тестирование команды /edit [testID] с неправильным id
     */
    @Test
    void testHandleEditInvalidId() {
        String result = testService.handleEdit(userId, "/edit aaa");

        assertEquals("Ошибка ввода!", result);
    }

    /**
     * Тестирование команды /del с правильным id
     */
    @Test
    void testHandleDel() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));

        String result = testService.handleDel(userId);

        assertEquals("Выберите тест:\n1)  id: 123 Название теста\n", result);
        verify(userService).changeStateById(userId, UserState.DELETE_TEST);
        userSession.setState(UserState.DELETE_TEST);
        result = testService.handleMessage(userSession, "123");
        assertEquals("Тест “Название теста” будет удалён, вы уверены? (Да/Нет)", result);

        verify(userService).changeStateById(userId, UserState.CONFIRM_DELETE_TEST);
        verify(userService).setCurrentTest(userId, test);
        userSession.setState(UserState.CONFIRM_DELETE_TEST);
        userSession.setCurrentTest(test);
        result = testService.handleMessage(userSession, "Да");
        assertEquals("Тест “Название теста” удалён", result);
        verify(userService).changeStateById(userId, UserState.DEFAULT);
        verify(testRepository).delete(test);
    }

    /**
     * Тестирование команды /del с неправильным id
     */
    @Test
    void testHandleDelInvalidId() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));

        String result = testService.handleDel(userId);

        assertEquals("Выберите тест:\n1)  id: 123 Название теста\n", result);
        verify(userService).changeStateById(userId, UserState.DELETE_TEST);
        userSession.setState(UserState.DELETE_TEST);
        result = testService.handleMessage(userSession, "ффв");
        assertEquals("Ошибка ввода!", result);
    }

    /**
     * Тестирование команды /del с id теста, которого нет у пользователя
     */
    @Test
    void testHandleDelNotFound() {
        when(userService.getTestsById(userId)).thenReturn(List.of(test));

        String result = testService.handleDel(userId);

        assertEquals("Выберите тест:\n1)  id: 123 Название теста\n", result);
        verify(userService).changeStateById(userId, UserState.DELETE_TEST);
        userSession.setState(UserState.DELETE_TEST);
        result = testService.handleMessage(userSession, "555");
        assertEquals("Тест не найден!", result);
    }

    /**
     * Тестирование обработки команды выбора теста через callback.
     */
    @Test
    void testHandleCallbackChooseCommand() {
        Update update = createMockCallbackUpdate("TEST CHOOSE 123");
        SendMessage expectedMessage = new SendMessage("123", "Вы выбрали тест “Название теста”. Всего вопросов: 1.");
        when(messageBuilder.createSendMessage(eq("123"), eq("Вы выбрали тест “Название теста”. Всего вопросов: 1."), isNull())).thenReturn(expectedMessage);
        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);
        when(testRepository.findById(testId)).thenReturn(java.util.Optional.of(test));

        SendMessage result = testService.handleCallback(update);

        assertEquals("Вы выбрали тест “Название теста”. Всего вопросов: 1.", result.getText());
        verify(userService).setCurrentTest(userId, test);
    }

    /**
     * Тестирование обработки команды начала теста через callback.
     */
    @Test
    void testHandleCallbackStartCommand() {
        Update update = createMockCallbackUpdate("TEST START");
        SendMessage expectedMessage = new SendMessage("123", "Вопрос 1/1: Вопрос\nВарианты ответа:\n1: Ответ\n\nВыберите один вариант ответа:");

        when(messageBuilder.createSendMessage(eq("123"), eq("Вопрос 1/1: Вопрос\nВарианты ответа:\n1: Ответ\n\nВыберите один вариант ответа:"), isNull())).thenReturn(expectedMessage);
        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);

        SendMessage result = testService.handleCallback(update);

        assertEquals("Вопрос 1/1: Вопрос\nВарианты ответа:\n1: Ответ\n\nВыберите один вариант ответа:", result.getText());
        verify(userService).getSession(userId);
    }

    /**
     * Тестирование обработки команды выхода из теста через callback.
     */
    @Test
    void testHandleCallbackExitCommand() {
        Update update = createMockCallbackUpdate("TEST EXIT");
        SendMessage expectedMessage = new SendMessage("123", "Вы вышли из теста");

        when(messageBuilder.createSendMessage(eq("123"), eq("Вы вышли из теста"), isNull())).thenReturn(expectedMessage);
        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);

        SendMessage result = testService.handleCallback(update);

        assertEquals("Вы вышли из теста", result.getText());
        verify(userService).changeStateById(userId, UserState.DEFAULT);
    }

    /**
     * Тестирование обработки команды завершения теста через callback.
     */
    @Test
    void testHandleCallbackFinishCommand() {
        Update update = createMockCallbackUpdate("TEST FINISH");
        SendMessage expectedMessage = new SendMessage("123", "Тест завершен!\nПравильных ответов: 1/1\nПроцент правильных ответов: 100%");

        when(messageBuilder.createSendMessage(eq("123"), eq("Тест завершен!\nПравильных ответов: 1/1\nПроцент правильных ответов: 100%"), isNull())).thenReturn(expectedMessage);
        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);
        when(userService.getCorrectAnswerCount(userId)).thenReturn(1);
        when(userService.getCountAnsweredQuestions(userId)).thenReturn(1);

        SendMessage result = testService.handleCallback(update);

        assertEquals("Тест завершен!\nПравильных ответов: 1/1\nПроцент правильных ответов: 100%", result.getText());
    }

    /**
     * Тестирование обработки недопустимой команды через callback.
     */
    @Test
    void testHandleCallbackInvalidCommand() {
        Update update = createMockCallbackUpdate("TEST INVALID");
        SendMessage expectedMessage = new SendMessage("123", "Ошибка: Неизвестная команда.");

        when(messageBuilder.createErrorMessage(eq("123"), eq("Неизвестная команда."))).thenReturn(expectedMessage);
        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);

        SendMessage result = testService.handleCallback(update);

        assertEquals("Ошибка: Неизвестная команда.", result.getText());
    }

    /**
     * Тестирование обработки команды редактирования вариантов ответа через callback.
     */
    @Test
    void testHandleCallbackEditAnswerCommand() {
        Update update = createMockCallbackUpdate("EDIT TEST ANSWER Ответ");
        QuestionEntity question = test.getQuestions().getFirst();

        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);
        when(userService.getCurrentQuestion(userId)).thenReturn(question);

        EditMessageText result = testService.handleCallbackEdit(update);

        assertEquals("Вопрос 1/1: Вопрос\nВарианты ответа:\n1: Ответ\n\nВыберите один вариант ответа:", result.getText());
    }

    /**
     * Тестирование обработки команды перехода к следующему вопросу через callback.
     */
    @Test
    void testHandleCallbackEditNextCommand() {
        Update update = createMockCallbackUpdate("EDIT TEST NEXT");
        QuestionEntity question = test.getQuestions().getFirst();

        AnswerEntity answer = new AnswerEntity();
        answer.setCorrect(true);
        answer.setAnswerText("Ответ2");
        QuestionEntity nextQuestion = new QuestionEntity();
        nextQuestion.getAnswers().add(answer);
        nextQuestion.setQuestion("Вопрос2");
        test.getQuestions().add(nextQuestion);

        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);
        when(userService.getCurrentQuestion(userId)).thenReturn(question);

        EditMessageText result = testService.handleCallbackEdit(update);

        assertEquals("Вопрос 2/2: Вопрос2\nВарианты ответа:\n1: Ответ2\n\nВыберите один вариант ответа:", result.getText());
    }

    /**
     * Тестирование обработки недопустимой команды редактирования через callback.
     */
    @Test
    void testHandleCallbackEditInvalidCommand() {
        Update update = createMockCallbackUpdate("EDIT TEST INVALID");

        when(userService.getSession(userId)).thenReturn(userSession);
        userSession.setCurrentTest(test);

        EditMessageText result = testService.handleCallbackEdit(update);

        assertEquals("Ошибка!", result.getText());
    }

    /**
     * Создание мок-объекта для обновления callback с заданными данными.
     *
     * @param callbackData Данные callback, которые будут использоваться в тесте.
     * @return Мок-объект обновления с заданными данными.
     */
    private Update createMockCallbackUpdate(String callbackData) {
        Update update = mock(Update.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getData()).thenReturn(callbackData);
        when(callbackQuery.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);

        return update;
    }
}