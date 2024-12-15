package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.processor.*;
import org.example.naumenteststgbot.processor.Add.*;
import org.example.naumenteststgbot.processor.Del.*;
import org.example.naumenteststgbot.processor.Edit.*;
import org.example.naumenteststgbot.processor.View.*;
import org.example.naumenteststgbot.repository.TestRepository;
import org.example.naumenteststgbot.repository.UserRepository;
import org.example.naumenteststgbot.repository.UserSessionRepository;
import org.example.naumenteststgbot.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестирование обработчика сообщений для управления тестами.
 * Проверяет функциональность создания, просмотра, редактирования и удаления тестов.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TestMessageProcessorTest {
    private TestRepository testRepository;
    private UserRepository userRepository;
    private UserSessionRepository sessionRepository;

    private final Util util = new Util();
    private final Long userId = 42L;
    private MessageHandler messageHandler;

    @BeforeAll
    void init() {
        testRepository = mock(TestRepository.class);
        userRepository = mock(UserRepository.class);
        sessionRepository = mock(UserSessionRepository.class);

        UserService userService = new UserService(userRepository);
        SessionService sessionService = new SessionService(sessionRepository, userService);
        StateService stateService = new StateService(sessionService);
        TestService testService = new TestService(testRepository, userService);

        HelpCommandProcessor helpCommandProcessor = new HelpCommandProcessor();
        StartCommandProcessor startCommandProcessor = new StartCommandProcessor(userService, helpCommandProcessor);
        AddCommandProcessor addCommandProcessor = new AddCommandProcessor(testService, stateService, sessionService);
        ViewCommandProcessor viewCommandProcessor = new ViewCommandProcessor(testService, stateService, util);
        EditCommandProcessor editCommandProcessor = new EditCommandProcessor(testService, util, sessionService, stateService);
        DelCommandProcessor delCommandProcessor = new DelCommandProcessor(stateService, testService, util);

        AddTestTitleProcessor addTestTitleProcessor = new AddTestTitleProcessor(stateService, sessionService, testService);
        AddTestDescriptionProcessor addTestDescriptionProcessor = new AddTestDescriptionProcessor(stateService, sessionService, testService);
        ViewTestProcessor viewTestProcessor = new ViewTestProcessor(stateService, viewCommandProcessor);
        EditTestProcessor editTestProcessor = new EditTestProcessor(stateService);
        EditTestTitleProcessor editTestTitleProcessor = new EditTestTitleProcessor(stateService, sessionService, testService);
        EditTestDescriptionProcessor editTestDescriptionProcessor = new EditTestDescriptionProcessor(stateService, sessionService, testService);
        DelTestProcessor delTestProcessor = new DelTestProcessor(stateService, testService, util, sessionService);
        ConfirmDelTest confirmDelTest = new ConfirmDelTest(stateService, sessionService, testService);

        List<MessageProcessor> processors = Arrays.asList(
                helpCommandProcessor,
                startCommandProcessor,
                addCommandProcessor,
                viewCommandProcessor,
                editCommandProcessor,
                delCommandProcessor,
                addTestTitleProcessor,
                addTestDescriptionProcessor,
                viewTestProcessor,
                editTestProcessor,
                editTestTitleProcessor,
                editTestDescriptionProcessor,
                delTestProcessor,
                confirmDelTest
        );

        messageHandler = new MessageHandler(processors);
    }

    @BeforeEach
    void clearMocks() {
        clearInvocations(testRepository, userRepository, sessionRepository);
    }

    /**
     * Создает тестовую сущность с заданными параметрами
     * @param userId Идентификатор пользователя
     * @param testId Идентификатор теста
     * @param title Название теста
     * @return Созданная тестовая сущность
     */
    private TestEntity createTest(Long userId, Long testId, String title) {
        TestEntity test = new TestEntity(userId, testId);
        test.setTitle(title);
        return test;
    }

    /**
     * Тестирует процесс создания нового теста
     */
    @Test
    void testAddTest() {
        UserSession session = new UserSession(userId);
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setSession(session);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.findById(userId)).thenReturn(Optional.of(session));
        when(testRepository.save(any(TestEntity.class))).thenAnswer(i -> i.getArgument(0));

        String response1 = messageHandler.handle("/add", userId);
        assertEquals("Введите название теста", response1);

        String response2 = messageHandler.handle("test", userId);
        assertEquals("Введите описание теста", response2);

        String response3 = messageHandler.handle("Тестовое описание", userId);
        assertEquals("Тест “test” создан! Количество вопросов: 0" +
                ". Для добавление вопросов используйте /add_question null, где null - идентификатор теста “test”."
                , response3);
    }

    /**
     * Подготавливает данные для использования в тестах
     */
    private void prepareTestData() {
        UserSession session = new UserSession(userId);
        TestEntity test1 = createTest(userId, 123L, "Математический тест");
        TestEntity test2 = createTest(userId, 312L, "Тест по знаниям ПДД");
        UserEntity user = new UserEntity(Arrays.asList(test1, test2));

        user.setId(userId);
        user.setSession(session);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.findById(userId)).thenReturn(Optional.of(session));
        when(testRepository.findById(123L)).thenReturn(Optional.of(test1));
        when(testRepository.findById(312L)).thenReturn(Optional.of(test2));
    }

    /**
     * Тестирует просмотр списка тестов
     */
    @Test
    void testViewTests() {
        prepareTestData();
        
        String response = messageHandler.handle("/view", userId);
        assertEquals("Выберите тест для просмотра:\n" +
                "1)  id: 123 Математический тест\n" +
                "2)  id: 312 Тест по знаниям ПДД\n", response);

        String response2 = messageHandler.handle("123", userId);
        assertEquals("Тест “Математический тест”. Всего вопросов: 0\n", response2);

        String response3 = messageHandler.handle("/view 312", userId);
        assertEquals("Тест “Тест по знаниям ПДД”. Всего вопросов: 0\n", response3);
    }

    /**
     * Тестирует обработку просмотра несуществующего теста
     */
    @Test
    void testViewTestsWithNotExistId() {
        prepareTestData();
        
        messageHandler.handle("/view", userId);
        String response2 = messageHandler.handle("999", userId);
        assertEquals("Тест не найден!", response2);

        String response = messageHandler.handle("/view 999", userId);
        assertEquals("Тест не найден!", response);
    }

    /**
     * Тестирует обработку некорректного идентификатора при просмотре теста
     */
    @Test
    void testViewTestsWithInvalidId() {
        prepareTestData();

        messageHandler.handle("/view", userId);
        String response = messageHandler.handle("abc", userId);
        assertEquals("Ошибка ввода!", response);

        String response2 = messageHandler.handle("/view abc", userId);
        assertEquals("Ошибка ввода!", response2);
    }
    

    /**
     * Тестирует процесс редактирования названия теста
     */
    @Test
    void testEditTestTitle() {
        prepareTestData();
        
        String response1 = messageHandler.handle("/edit 123", userId);
        assertEquals("Вы выбрали тест “Математический тест”. " +
                "Что вы хотите изменить?\n" +
                "1: Название теста\n" +
                "2: Описание теста\n", response1);

        String response2 = messageHandler.handle("1", userId);
        assertEquals("Введите новое название теста", response2);

        String response3 = messageHandler.handle("Новый математический тест", userId);
        assertEquals("Название изменено на “Новый математический тест”", response3);

        verify(testRepository, times(1)).save(argThat(savedTest -> 
            savedTest.getId().equals(123L) && 
            savedTest.getTitle().equals("Новый математический тест")
        ));
    }

    /**
     * Тестирует обработку редактирования с некорректным идентификатора теста
     */
    @Test
    void testEditTestWithInvalidId() {
        prepareTestData();
        
        String response1 = messageHandler.handle("/edit 999", userId);
        assertEquals("Тест не найден!", response1);

        String response2 = messageHandler.handle("/edit abc", userId);
        assertEquals("Ошибка ввода!", response2);
    }

    /**
     * Тестирует процесс удаления теста
     */
    @Test
    void testDeleteTest() {
        prepareTestData();
        
        String response1 = messageHandler.handle("/del", userId);
        assertEquals("Выберите тест:\n" +
                "1)  id: 123 Математический тест\n" +
                "2)  id: 312 Тест по знаниям ПДД\n", response1);

        String response2 = messageHandler.handle("123", userId);
        assertEquals("Тест “Математический тест” будет удалён, вы уверены? (Да/Нет)", response2);

        String response3 = messageHandler.handle("Да", userId);
        assertEquals("Тест “Математический тест” удалён", response3);

        verify(testRepository, times(1)).delete(argThat(deletedTest -> 
            deletedTest.getId().equals(123L) && 
            deletedTest.getTitle().equals("Математический тест")
        ));
    }

    /**
     * Тестирует отмену удаления теста
     */
    @Test
    void testDeleteTestCancel() {
        prepareTestData();
        
        messageHandler.handle("/del", userId);
        messageHandler.handle("123", userId);
        
        String response = messageHandler.handle("Нет", userId);
        assertEquals("Тест “Математический тест” не удалён", response);
        
        verify(testRepository, never()).delete(any(TestEntity.class));
    }

    /**
     * Тестирует обработку некорректного идентификатора при удалении теста
     */
    @Test
    void testDeleteTestWithInvalidId() {
        prepareTestData();
        
        messageHandler.handle("/del", userId);
        
        String response1 = messageHandler.handle("999", userId);
        assertEquals("Тест не найден!", response1);

        String response2 = messageHandler.handle("abc", userId);
        assertEquals("Введите число!", response2);
    }

    /**
     * Тестирует редактирование описания теста
     */
    @Test
    void testEditTestDescription() {
        prepareTestData();
        
        String response1 = messageHandler.handle("/edit 123", userId);
        assertEquals("Вы выбрали тест “Математический тест”. Что вы хотите изменить?\n" +
                "1: Название теста\n" +
                "2: Описание теста\n", response1);

        String response2 = messageHandler.handle("2", userId);
        assertEquals("Введите новое описание теста", response2);

        String response3 = messageHandler.handle("Новое описание математического теста", userId);
        assertEquals("Описание изменено на “Новое описание математического теста”", response3);

        verify(testRepository, times(1)).save(argThat(savedTest -> 
            savedTest.getId().equals(123L) && 
            savedTest.getDescription().equals("Новое описание математического теста")
        ));
    }

}