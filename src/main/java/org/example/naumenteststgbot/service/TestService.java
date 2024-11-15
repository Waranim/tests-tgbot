package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для обработки команд и сообщений, связанных с тестом
 */
@Service
public class TestService {
    private final TestRepository testRepository;
    private final UserService userService;

    public TestService(TestRepository testRepository, UserService userService) {
        this.testRepository = testRepository;
        this.userService = userService;
    }

    /**
     * Обработать команду добавления теста
     */
    public String handleAdd(Long userId) {
        TestEntity test = createTest(userId);
        userService.setState(userId, UserState.ADD_TEST_TITLE);
        userService.setCurrentTest(userId, test);
        return "Введите название теста";
    }

    /**
     * Обработать команду просмотра теста
     */
    @Transactional
    public String handleView(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = userService.getTestsById(userId);

        if (parts.length == 1) {
            userService.setState(userId, UserState.VIEW_TEST);
            return "Выберите тест для просмотра:\n"
                    + testsListToString(tests);
        } else if (isNumber(parts[1])){
            userService.setState(userId, UserState.DEFAULT);
            Long testId = Long.parseLong(parts[1]);
            TestEntity test = getTest(testId);
            if (test == null || !tests.contains(test)) return "Тест не найден!";
            return testToString(test);
        }
        return "Ошибка ввода";
    }

    /**
     * Обработать команду редактирования теста
     */
    @Transactional
    public String handleEdit(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = userService.getTestsById(userId);
        if (parts.length == 1)
            return "Используйте команду вместе с идентификатором теста!";
        else if (!isNumber(parts[1]))
            return "Ошибка ввода!";
        Long testId = Long.parseLong(parts[1]);
        TestEntity test = getTest(testId);
        if (test == null || !tests.contains(test))
            return "Тест не найден!";
        userService.setCurrentTest(userId, test);
        userService.setState(userId, UserState.EDIT_TEST);
        return String.format("""
                Вы выбрали тест “%s”. Что вы хотите изменить?
                1: Название теста
                2: Описание теста
                """, test.getTitle());
    }

    /**
     * Обработать команду удаления теста
     */
    @Transactional
    public String handleDel(Long id) {
        userService.setState(id, UserState.DELETE_TEST);
        return "Выберите тест:\n" + testsListToString(userService.getTestsById(id));
    }

    /**
     * Получить тест по идентификатору
     * @param id Идентификатор теста
     * @return тест или null, если не найден
     */
    public TestEntity getTest(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    /**
     * Создать тест
     * @param creatorId Идентификатор создателя
     * @return Созданный тесть
     */
    private TestEntity createTest(Long creatorId){
        TestEntity test = new TestEntity(creatorId);
        return testRepository.save(test);
    }

    /**
     * Обработать сообщение, в зависимости от состояния пользователя
     * @param userSession Сессия пользователя
     * @param message  сообщение
     * @return Ответ пользователю
     */
    @Transactional
    public String handleMessage(UserSession userSession, String message) {
        UserState userState = userSession.getState();
        Long userId = userSession.getUserId();
        TestEntity currentTest = userSession.getCurrentTest();
        String response = "Ошибка";
        switch (userState) {
            case DEFAULT:
                break;
            case ADD_TEST_TITLE:
                currentTest.setTitle(message);
                response = "Введите описание теста";
                userService.setState(userId, UserState.ADD_TEST_DESCRIPTION);
                break;
            case ADD_TEST_DESCRIPTION:
                currentTest.setDescription(message);
                response = String.format("Тест “%s” создан! Количество вопросов: 0. Для добавление вопросов используйте /add_question %s, где %s - идентификатор теста “%s”.", currentTest.getTitle(), currentTest.getId(), currentTest.getId(), currentTest.getTitle());
                userService.setState(userId, UserState.DEFAULT);
                break;
            case EDIT_TEST:
                if(message.equals("1")){
                    response = "Введите новое название теста";
                    userService.setState(userId, UserState.EDIT_TEST_TITLE);
                }
                else if(message.equals("2")){
                    response = "Введите новое описание теста";
                    userService.setState(userId, UserState.EDIT_TEST_DESCRIPTION);
                }
                break;
            case EDIT_TEST_TITLE:
                currentTest.setTitle(message);
                userService.setState(userId, UserState.DEFAULT);
                response = String.format("Название изменено на “%s”", message);
                break;
            case EDIT_TEST_DESCRIPTION:
                currentTest.setDescription(message);
                userService.setState(userId, UserState.DEFAULT);
                response = String.format("Описание изменено на “%s”", message);
                break;
            case DELETE_TEST:
                TestEntity test = getTest(Long.parseLong(message));
                List<TestEntity> tests = userService.getTestsById(userId);
                if (test == null || !tests.contains(test)) return "Тест не найден!";
                response = String.format("Тест “%s” будет удалён, вы уверены? (Да/Нет)", test.getTitle());
                userService.setCurrentTest(userId, test);
                userService.setState(userId, UserState.CONFIRM_DELETE_TEST);
                break;
            case CONFIRM_DELETE_TEST:
                message = message.toLowerCase();
                if (message.equals("да"))
                {
                    userService.setCurrentTest(userId, null);
                    testRepository.delete(currentTest);
                    return String.format("Тест “%s” удалён", currentTest.getTitle());
                }
                else{
                    return String.format("Тест “%s” не удалён", currentTest.getTitle());
                }
            case VIEW_TEST:
                return handleView(userId, "/view " + message);

        }
        if(currentTest != null)
            testRepository.save(currentTest);
        return response;
    }


    /**
     * Получить развернутое строковое представление сущности теста
     */
    private String testToString(TestEntity test) {
        List<QuestionEntity> questions = test.getQuestions();
        StringBuilder response = new StringBuilder(String.format("Тест “%s”. Всего вопросов: %s\n",  test.getTitle(), questions.size()));
        for (QuestionEntity question : questions) {
            response.append("Вопрос: %s\nВарианты ответов:\n".formatted(question.getQuestion()));
            List<AnswerEntity> answers = question.getAnswers();
            AnswerEntity correctAnswer = null;
            for (int i = 0; i < answers.size(); i++) {
                var answer = answers.get(i);
                response.append("%s - %s\n".formatted(i+1, answer.getAnswerText()));
                if(answer.isCorrect()) correctAnswer = answer;
            }
            response.append("Правильный вариант: ").append(correctAnswer.getAnswerText()).append("\n\n");
        }
        return response.toString();
    }

    /**
     * Получить строковое представление списка тестов
     */
    private String testsListToString(List<TestEntity> tests) {
        StringBuilder response = new StringBuilder();
        for(int i = 0; i < tests.size(); i++) {
            TestEntity currentTest = tests.get(i);
            response.append(String.format("%s)  id: %s %s\n", i+1, currentTest.getId(), currentTest.getTitle()));
        }
        return response.toString();
    }

    /**
     * Узнать, находится ли в строке только лишь число
     * @return true - если только цифры в строке, false - все остальные случаи.
     */
    private boolean isNumber(String number) {
        return number.matches("^-?\\d+$");
    }
}