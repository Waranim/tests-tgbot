package org.example.bot.service;

import org.example.bot.entity.TestEntity;
import org.example.bot.entity.UserContext;
import org.example.bot.entity.UserEntity;
import org.example.bot.processor.share.InfoCommandProcessor;
import org.example.bot.processor.share.*;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.NumberUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала шейринга тестов
 * Проверяет работу всех обработчиков, связанных с передачей тестов между пользователями
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShareTest {

    /**
     * Сервис для управления контекстом
     */
    @Mock
    private ContextService contextService;

    /**
     * Сервис для управления состояниями
     */
    @Mock
    private StateService stateService;

    /**
     * Сервис для управления тестами
     */
    @Mock
    private TestService testService;

    /**
     * Сервис для управления пользователями
     */
    @Mock
    private UserService userService;

    /**
     * Утилитарный класс
     */
    private final NumberUtils numberUtils = new NumberUtils();

    /**
     * Обработчик команды /share
     */
    private ShareCommandProcessor shareCommandProcessor;

    /**
     * Обработчик выбора теста для шейринга
     */
    private ShareChooseTestProcessor shareChooseTestProcessor;

    /**
     * Обработчик выбора пользователя для шейринга
     */
    private ShareChooseUserProcessor shareChooseUserProcessor;

    /**
     * Обработчик команды просмотра расшаренных тестов
     */
    private SharedTestsCommandProcessor sharedTestsCommandProcessor;

    /**
     * Обработчик выбора теста для отписки
     */
    private ShareUnsubscribeChoseTestProcessor shareUnsubscribeChoseTestProcessor;

    /**
     * Обработчик отписки от теста
     */
    private ShareUnsubscribeTestProcessor shareUnsubscribeTestProcessor;

    /**
     * Обработчик команды получения идентификатора пользователя
     */
    private InfoCommandProcessor infoCommandProcessor;

    /**
     * Идентификатор тестового пользователя
     */
    private final Long userId = 1L;

    /**
     * Тестовая сущность теста
     */
    private TestEntity testEntity;

    /**
     * Сущность пользователя-владельца теста
     */
    private UserEntity userEntity;

    /**
     * Сущность пользователя-получателя теста
     */
    private UserEntity recipientUser;

    /**
     * Инициализация тестовых данных
     */
    @BeforeAll
    void init() {
        testEntity = new TestEntity(userId, 1L);
        testEntity.setTitle("тест");
        testEntity.setAccessOpen(true);

        userEntity = new UserEntity(userId, "testUser", new UserContext());
        userEntity.getTests().add(testEntity);

        recipientUser = new UserEntity(2L, "recipient", new UserContext());
    }

    /**
     * Инициализация процессоров перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        shareCommandProcessor = new ShareCommandProcessor(testService);
        shareChooseTestProcessor = new ShareChooseTestProcessor(testService, contextService, stateService);
        shareChooseUserProcessor = new ShareChooseUserProcessor(stateService, numberUtils, userService, contextService, testService);
        sharedTestsCommandProcessor = new SharedTestsCommandProcessor(userService);
        shareUnsubscribeChoseTestProcessor = new ShareUnsubscribeChoseTestProcessor(userService, testService);
        shareUnsubscribeTestProcessor = new ShareUnsubscribeTestProcessor(userService, testService);
        infoCommandProcessor = new InfoCommandProcessor();
    }

    /**
     * Очистка моков после каждого теста
     */
    @AfterEach
    void clearMocks() {
        clearInvocations(contextService, stateService, testService, userService);
    }

    /**
     * Тестирует команду /share
     */
    @Test
    void shouldProcessShareCommand() {
        List<TestEntity> tests = List.of(testEntity);
        when(testService.getTestsByUserId(userId)).thenReturn(Optional.of(tests));

        BotResponse response = shareCommandProcessor.process(userId, "/share");

        assertEquals("Выберите тест: ", response.getMessage());
        assertEquals("тест", response.getButtons().getFirst().getFirst().text());
        verify(testService).getTestsByUserId(userId);
    }

    /**
     * Тестирует команду /info
     */
    @Test
    void shouldProcessInfoCommand() {
        BotResponse response = infoCommandProcessor.process(userId, "/info");
        assertEquals("Ваш идентификатор: 1", response.getMessage());
    }

    /**
     * Тестирует выбор теста для шейринга
     */
    @Test
    void shouldProcessTestChoice() {
        when(testService.getTest(1L)).thenReturn(Optional.of(testEntity));

        BotResponse response = shareChooseTestProcessor.process(userId, "SHARE_CHOOSE_TEST 1");

        assertEquals("Введите идентификатор пользователя (его можно посмотреть командой /info)", response.getMessage());
        verify(contextService).setCurrentTest(userId, testEntity);
        verify(stateService).changeStateById(userId, UserState.CHOOSE_USER);
    }

    /**
     * Тестирует выбор пользователя для шейринга
     */
    @Test
    void shouldProcessUserChoice() {
        when(userService.getUserById(2L)).thenReturn(Optional.of(recipientUser));
        when(contextService.getCurrentTest(userId)).thenReturn(Optional.of(testEntity));

        BotResponse response = shareChooseUserProcessor.process(userId, "2");

        assertEquals("Пользователь 2 получил доступ к тесту", response.getMessage());
        verify(userService).addReceivedTest(2L, testEntity);
        verify(testService).update(testEntity);
    }

    /**
     * Тестирует команду просмотра расшейренных тестов
     */
    @Test
    void shouldShowSharedTests() {
        List<TestEntity> receivedTests = List.of(testEntity);
        when(userService.getUserById(userId)).thenReturn(Optional.ofNullable(userEntity));
        when(userService.getOpenReceivedTests(userId)).thenReturn(receivedTests);

        BotResponse response = sharedTestsCommandProcessor.process(userId, "/shared_tests");

        assertEquals("Выберите тест:", response.getMessage());
        assertEquals("тест (testUser)", response.getButtons().getFirst().getFirst().text());
    }

    /**
     * Тестирует выбор теста для отписки
     */
    @Test
    void shouldProcessUnsubscribeTestChoice() {
        List<TestEntity> receivedTests = List.of(testEntity);
        when(userService.getOpenReceivedTests(userId)).thenReturn(receivedTests);
        when(testService.getTest(1L)).thenReturn(Optional.of(testEntity));
        when(userService.getUserById(userId)).thenReturn(Optional.of(userEntity));

        BotResponse response = shareUnsubscribeChoseTestProcessor.process(userId, "SHARE_UNSUBSCRIBE_CHOOSE_TEST 1");

        assertEquals("Вы выбрали “тест (testUser)”. Всего вопросов: 0.", response.getMessage());
        assertEquals("Отписаться", response.getButtons().getFirst().getFirst().text());
    }

    /**
     * Тестирует процесс отписки от теста
     */
    @Test
    void shouldProcessUnsubscribe() {
        List<TestEntity> receivedTests = List.of(testEntity);
        when(userService.getOpenReceivedTests(userId)).thenReturn(receivedTests);
        when(testService.getTest(1L)).thenReturn(Optional.of(testEntity));
        when(userService.getUserById(userId)).thenReturn(Optional.of(userEntity));
        when(userService.getUserById(testEntity.getCreatorId())).thenReturn(Optional.of(userEntity));

        BotResponse response = shareUnsubscribeTestProcessor.process(userId, "SHARE_UNSUBSCRIBE_TEST 1");

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Вы отписались от теста"));
        verify(userService).removeReceivedTest(userId, testEntity);
        verify(testService).update(testEntity);
    }

    /**
     * Тестирует обработку некорректного ID пользователя
     */
    @Test
    void shouldHandleInvalidUserId() {
        BotResponse response = shareChooseUserProcessor.process(userId, "invalid");
        assertEquals("Некорректный id пользователя", response.getMessage());
    }

    /**
     * Тестирует случай, когда пользователь не найден
     */
    @Test
    void shouldHandleUserNotFound() {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        BotResponse response = shareChooseUserProcessor.process(userId, "999");

        assertEquals("Пользователь не найден", response.getMessage());
    }

    /**
     * Тестирует случай с закрытым доступом к тесту
     */
    @Test
    void shouldHandleClosedTest() {
        testEntity.setAccessOpen(false);
        when(testService.getTest(1L)).thenReturn(Optional.of(testEntity));

        BotResponse response = shareChooseTestProcessor.process(userId, "SHARE_CHOOSE_TEST 1");

        assertEquals("У теста закрыт доступ!", response.getMessage());
        verify(testService).getTest(1L);
    }

    /**
     * Тестирует случай, когда у пользователя уже есть доступ к тесту
     */
    @Test
    void shouldHandleExistingAccess() {
        when(userService.getUserById(2L)).thenReturn(Optional.of(recipientUser));
        when(contextService.getCurrentTest(userId)).thenReturn(Optional.of(testEntity));
        recipientUser.getReceivedTests().add(testEntity);

        BotResponse response = shareChooseUserProcessor.process(userId, "2");

        assertEquals("Пользователь уже имеет доступ к этому тесту", response.getMessage());
        verify(userService).getUserById(2L);
        recipientUser.getReceivedTests().clear();
    }

    /**
     * Тестирует случай, когда тесты не найдены
     */
    @Test
    void shouldHandleNoTests() {
        when(testService.getTestsByUserId(userId)).thenReturn(Optional.empty());

        BotResponse response = shareCommandProcessor.process(userId, "/share");

        assertEquals("Тесты не найдены!", response.getMessage());
        verify(testService).getTestsByUserId(userId);
    }

    /**
     * Тестирует случай с некорректным ID теста
     */
    @Test
    void shouldHandleInvalidTestId() {
        when(testService.getTest(999L)).thenReturn(Optional.empty());

        BotResponse response = shareChooseTestProcessor.process(userId, "SHARE_CHOOSE_TEST 999");

        assertEquals("Тест не найден", response.getMessage());
        verify(testService).getTest(999L);
    }

    /**
     * Тестирует отсутствие доступа к тесту при отписке
     */
    @Test
    void shouldHandleNoAccessToUnsubscribe() {
        List<TestEntity> receivedTests = new ArrayList<>();
        when(userService.getOpenReceivedTests(userId)).thenReturn(receivedTests);
        when(testService.getTest(1L)).thenReturn(Optional.of(testEntity));

        BotResponse response = shareUnsubscribeTestProcessor.process(userId, "SHARE_UNSUBSCRIBE_TEST 1");

        assertEquals("У вас нет доступа к тесту", response.getMessage());
        verify(userService).getOpenReceivedTests(userId);
    }
}