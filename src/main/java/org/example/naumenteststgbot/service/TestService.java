package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            return "Выберите тест для просмотра:\n"
                    + testsListToString(tests);
        } else if (parts[1].matches("^-?\\d+$")){
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

    public TestEntity getTest(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    private TestEntity createTest(Long creatorId){
        TestEntity test = new TestEntity(creatorId);
        return testRepository.save(test);
    }

    @Transactional
    public String getResponseMessage(UserSession userSession, String text) {
        UserState userState = userSession.getState();
        Long userId = userSession.getUserId();
        TestEntity currentTest = userSession.getCurrentTest();
        String response = "Ошибка";
        switch (userState) {
            case DEFAULT:
                break;
            case ADD_TEST_TITLE:
                currentTest.setTitle(text);
                response = "Введите описание теста";
                userService.setState(userId, UserState.ADD_TEST_DESCRIPTION);
                break;
            case ADD_TEST_DESCRIPTION:
                currentTest.setDescription(text);
                response = String.format("Тест “%s” создан! Количество вопросов: 0. Для добавление вопросов используйте /add_question %s, где %s - идентификатор теста “%s”.", currentTest.getTitle(), currentTest.getId(), currentTest.getId(), currentTest.getTitle());
                userService.setState(userId, UserState.DEFAULT);
                break;
            case EDIT_TEST:
                if(text.equals("1")){
                    response = "Введите новое название теста";
                    userService.setState(userId, UserState.EDIT_TEST_TITLE);
                }
                else if(text.equals("2")){
                    response = "Введите новое описание теста";
                    userService.setState(userId, UserState.EDIT_TEST_DESCRIPTION);
                }
                break;
            case EDIT_TEST_TITLE:
                currentTest.setTitle(text);
                userService.setState(userId, UserState.DEFAULT);
                response = String.format("Название изменено на “%s”", text);
                break;
            case EDIT_TEST_DESCRIPTION:
                currentTest.setDescription(text);
                userService.setState(userId, UserState.DEFAULT);
                response = String.format("Описание изменено на “%s”", text);
                break;
            case DELETE_TEST:
                TestEntity test = getTest(Long.parseLong(text));
                List<TestEntity> tests = userService.getTestsById(userId);
                if (test == null || !tests.contains(test)) return "Тест не найден!";
                response = String.format("Тест “%s” будет удалён, вы уверены? (Да/Нет)", currentTest.getTitle());
                userService.setState(userId, UserState.CONFIRM_DELETE_TEST);
                break;
            case CONFIRM_DELETE_TEST:
                text = text.toLowerCase();
                if (text.equals("да"))
                {
                    userService.setCurrentTest(userId, null);
                    testRepository.delete(currentTest);
                    return String.format("Тест “%s” удалён", currentTest.getTitle());
                }
                else{
                    return String.format("Тест “%s” не удалён", currentTest.getTitle());
                }

        }
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
            response.append(question.toString()).append('\n');
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
}