package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.QuestionRepository;
import org.example.naumenteststgbot.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для обработки команд и сообщений, связанных с тестом
 */
@Service
public class TestService {
    /**
     * Репозиторий для тестов
     */
    private final TestRepository testRepository;

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Сервис для работы с inline клавиатурой
     */
    private final KeyboardService keyboardService;

    /**
     * Создание сообщений
     */
    private final MessageBuilder messageBuilder;

    /**
     * Конструктор сервиса тестов
     * @param testRepository Репозиторий для тестов
     * @param userService Сервис пользователей
     */
    public TestService(TestRepository testRepository, UserService userService, KeyboardService keyboardService, MessageBuilder messageBuilder) {
        this.testRepository = testRepository;
        this.userService = userService;
        this.keyboardService = keyboardService;
        this.messageBuilder = messageBuilder;
    }

    /**
     * Обработать команду добавления теста
     */
    @Transactional
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
    public SendMessage handleView(String chatId, Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = userService.getTestsById(userId);

        if (parts.length == 1) {
            userService.setState(userId, UserState.VIEW_TEST);
            return messageBuilder.createSendMessage(chatId,"Выберите тест для просмотра:\n"+ testsListToString(tests),null);
        } else if (isNumber(parts[1])){
            userService.setState(userId, UserState.DEFAULT);
            Long testId = Long.parseLong(parts[1]);
            TestEntity test = getTest(testId);
            if (test == null || !tests.contains(test))  return  messageBuilder.createSendMessage(chatId,"Тест не найден!",null);
            return messageBuilder.createSendMessage(chatId,testToString(test),null);
        }
        return messageBuilder.createSendMessage(chatId,"Ошибка ввода!",null);
    }

    /**
     * Обработать команду редактирования теста
     */
    @Transactional
    public SendMessage handleEdit(String chatId,Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = userService.getTestsById(userId);
        if (parts.length == 1)
            return messageBuilder.createSendMessage(chatId,"Используйте команду вместе с идентификатором теста!",null);
        else if (!isNumber(parts[1]))
            return messageBuilder.createSendMessage(chatId,"Ошибка ввода!",null);
        Long testId = Long.parseLong(parts[1]);
        TestEntity test = getTest(testId);
        if (test == null || !tests.contains(test))
            messageBuilder.createSendMessage(chatId,"Тест не найден!",null);
        userService.setCurrentTest(userId, test);
        userService.setState(userId, UserState.EDIT_TEST);
        List<String> buttonsText = List.of("Название теста","Описание теста");
        List<String> callback = List.of("changeText","changeDescription");
        return messageBuilder.createSendMessage(chatId,"Вы выбрали тест “%s”. Что вы хотите изменить?” ".formatted(test.getTitle()),keyboardService.createReply(buttonsText,callback,"TEST"));
    }

    /**
     * Обработать команду удаления теста
     */
    @Transactional
    public SendMessage handleDel(String chatId,Long id) {
        userService.setState(id, UserState.DELETE_TEST);
        return messageBuilder.createSendMessage(chatId,"Выберите тест:\n"+ testsListToString(userService.getTestsById(id)),null);
    }

    /**
     * Обработать команду /test
     */
    public SendMessage handleTest(Long id, String chatId) {

        List<TestEntity> tests = userService.getTestsById(id);
        List<String> testsTitles = tests.stream().map(TestEntity::getTitle).toList();
        List<String> testsIds = tests.stream().map(t -> t.getId().toString()).toList();
        SendMessage message = new SendMessage();
        message.setReplyMarkup(keyboardService.createReply(testsTitles, testsIds, "TEST CHOOSE"));
        message.setText("Выберите тест:");
        message.setChatId(chatId);
        userService.setState(id, UserState.INLINE_KEYBOARD);
        return message;
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
    public SendMessage handleMessage(String chatId,UserSession userSession, String message) {
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
                if(!isNumber(message)) {
                    response = "Ошибка ввода!";
                    break;
                }
                TestEntity test = getTest(Long.parseLong(message));
                List<TestEntity> tests = userService.getTestsById(userId);
                if (test == null || !tests.contains(test))
                    return messageBuilder.createSendMessage(chatId,"Тест не найден!",null);
                userService.setCurrentTest(userId, test);
                userService.setState(userId, UserState.CONFIRM_DELETE_TEST);
                List<String> buttonsText = List.of("Да", "Нет");
                List<String> callback = List.of("confirmDeleteYes", "confirmDeleteNo");
                return messageBuilder.createSendMessage(chatId, "Тест “%s” будет удалён, вы уверены?".formatted(test.getTitle()), keyboardService.createReply(buttonsText, callback, "TEST")
                );
            case CONFIRM_DELETE_TEST:
                message = message.toLowerCase();
                userService.setState(userId, UserState.DEFAULT);
                if (message.equals("да"))
                {
                    userService.setCurrentTest(userId, null);
                    userService.setCurrentQuestion(userId, null);
                    testRepository.delete(currentTest);
                    return messageBuilder.createSendMessage(chatId,"Тест “%s” удалён".formatted(currentTest.getTitle()),null);

                }
                else{
                    return messageBuilder.createSendMessage(chatId,"Тест “%s” не удалён".formatted(currentTest.getTitle()),null);
                }
            case VIEW_TEST:
                return handleView(chatId,userId, "/view " + message);

        }
        if(currentTest != null)
            testRepository.save(currentTest);
        return messageBuilder.createSendMessage(chatId,response,null);
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

    /**
     * Обработать Callback query связанный с тестами
     */
    @Transactional
    public SendMessage handleCallback(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();
        String[] callbackDataParts = callbackData.split(" ");

        if (callbackDataParts.length < 2) {
            return messageBuilder.createErrorMessage(chatId, "Некорректные данные в callback.");
        }

        Long userId = update.getCallbackQuery().getFrom().getId();
        TestEntity currentTest = userService.getSession(userId).getCurrentTest();

        if (currentTest == null) {
            return messageBuilder.createErrorMessage(chatId, "Текущий тест не найден.");
        }

        List<QuestionEntity> questions = currentTest.getQuestions();

        String command = callbackDataParts[1];
        switch (command) {
            case "changeText":
                userService.setState(userId, UserState.EDIT_TEST_TITLE);
                return messageBuilder.createSendMessage(chatId,"Введите новое название теста",null);
            case "changeDescription":
                userService.setState(userId, UserState.EDIT_TEST_DESCRIPTION);
                return messageBuilder.createSendMessage(chatId,"Введите новое описание теста",null);
            case "confirmDeleteYes":
                TestEntity testToDelete = userService.getSession(userId).getCurrentTest();
                userService.setCurrentTest(userId, null);
                userService.setCurrentQuestion(userId, null);
                testRepository.delete(testToDelete);
                userService.setState(userId, UserState.DEFAULT);
                return messageBuilder.createSendMessage(chatId, String.format("Вопрос “%s” успешно удалён.", testToDelete.getTitle()),null);

            case "confirmDeleteNo":
                userService.setState(userId, UserState.DEFAULT);
                return messageBuilder.createSendMessage(chatId, "Удаление вопроса отменено.",null);

            case "CHOOSE":
                return handleTestChoose(chatId, userId, callbackDataParts[2]);
            case "START":
                return handleTestStart(chatId, userId, questions);
            case "END":
                return handleTestEnd(chatId, userId);
            default:
                return messageBuilder.createErrorMessage(chatId, "Неизвестная команда.");
        }
    }

    /**
     * Обработка выбора теста
     */
    private SendMessage handleTestChoose(String chatId, Long userId, String testIdString) {
        Long testId = Long.parseLong(testIdString);

        TestEntity test = getTest(testId);
        if (test == null) {
            return messageBuilder.createErrorMessage(chatId, "Тест с указанным ID не найден.");
        }

        userService.setCurrentTest(userId, test);
        return messageBuilder.createSendMessage(chatId, String.format(
                "Вы выбрали тест “%s”. Всего вопросов: %d.",
                test.getTitle(), test.getQuestions().size()
        ), keyboardService.createReply("Начать", "START", "TEST"));
    }

    /**
     * Обработка начала теста
     */
    private SendMessage handleTestStart(String chatId, Long userId, List<QuestionEntity> questions) {
        if (questions.isEmpty()) {
            return messageBuilder.createErrorMessage(chatId, "Вопросы в тесте отсутствуют.");
        }

        userService.setCurrentQuestion(userId, questions.getFirst());
        return createQuestionMessage(chatId, 0, questions);
    }

    /**
     * Обработка конца теста
     */
    private SendMessage handleTestEnd(String chatId, Long userId) {
        userService.setState(userId, UserState.DEFAULT);
        return messageBuilder.createSendMessage(chatId, "Тест завершен!", null);
    }

    /**
     * Создать сообщение с вопросом
     */
    private SendMessage createQuestionMessage(String chatId, int questionIndex, List<QuestionEntity> questions) {
        QuestionEntity question = questions.get(questionIndex);
        List<String> answers = question.getAnswers().stream().map(AnswerEntity::getAnswerText).collect(Collectors.toList());
        List<String> callbacks = question.getAnswers().stream()
                .map(a -> "ANSWER " + a.getAnswerText()).collect(Collectors.toList());

        if (questionIndex < questions.size() - 1) {
            answers.add("След. Вопрос");
            callbacks.add("NEXT " + (questionIndex + 1));
        } else {
            answers.add("Завершить");
            callbacks.add("END");
        }

        return messageBuilder.createSendMessage(chatId,
                String.format("Вопрос %d/%d: %s", questionIndex + 1, questions.size(), question.getQuestion()),
                keyboardService.createReply(answers, callbacks, "EDIT TEST"));
    }

    /**
     * Обработать callback query с редактированием сообщения
     */
    @Transactional
    public EditMessageText handleCallbackEdit(Update update) {
        EditMessageText editMessageText = createEditMessageText(update);
        String callbackData = update.getCallbackQuery().getData();
        String[] callbackDataParts = callbackData.split(" ");
        Long userId = update.getCallbackQuery().getFrom().getId();
        TestEntity currentTest = userService.getSession(userId).getCurrentTest();
        List<QuestionEntity> questions = currentTest.getQuestions();
        QuestionEntity previousQuestion = userService.getCurrentQuestion(userId);
        switch (callbackDataParts[2]) {
            case "ANSWER":
                handleAnswer(callbackDataParts[3], previousQuestion, questions, editMessageText);
                break;
            case "NEXT":
                handleNext(questions.indexOf(previousQuestion)+1, questions, userId, editMessageText);
                break;
            default:
                editMessageText.setText("Ошибка!");
        }
    return editMessageText;
    }

    /**
     * Создать объект EditMessageText и поместить в него идентификаторы чата и сообщения из update
     */
    private static EditMessageText createEditMessageText(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        return editMessageText;
    }

    /**
     * Обработать ответ на inline клавиатуре
     */
    private void handleAnswer(String answer, QuestionEntity previousQuestion, List<QuestionEntity> questions, EditMessageText editMessageText) {
        int nextQuestionIndex = questions.indexOf(previousQuestion) + 1;
        List<String> buttonsText = previousQuestion.getAnswers().stream().map(a ->
        {if (a.getAnswerText().equals(answer))
            if (a.isCorrect())
                return a.getAnswerText() + " " + "✅";
            else
                return a.getAnswerText() + " " + "❌";
         else return a.getAnswerText();
        }).collect(Collectors.toCollection(ArrayList::new));
        List<String> buttonsCallback = previousQuestion.getAnswers().stream()
                .map(a -> "EDIT IGNORE " + a.getAnswerText())
                .collect(Collectors.toCollection(ArrayList::new));
        if(nextQuestionIndex < questions.size()) {
            buttonsText.add("След. Вопрос");
            buttonsCallback.add("EDIT TEST NEXT");
        }
        else {
            buttonsText.add("Завершить");
            buttonsCallback.add("TEST END");
        }
        editMessageText.setText("Вопрос %s/%s: %s".formatted(nextQuestionIndex, questions.size(), previousQuestion.getQuestion()));
        editMessageText.setReplyMarkup(keyboardService.createReply(buttonsText, buttonsCallback, ""));
    }

    /**
     * Обработать переход на следующий вопрос
     */
    private void handleNext(int currentQuestionIndex, List<QuestionEntity> questions, Long userId, EditMessageText editMessageText) {
        QuestionEntity currentQuestion = questions.get(currentQuestionIndex);
        List<String> buttonsText = currentQuestion.getAnswers().stream().map(AnswerEntity::getAnswerText).collect(Collectors.toCollection(ArrayList::new));
        List<String> buttonsCallback = currentQuestion.getAnswers().stream()
                .map(a -> "EDIT TEST ANSWER " + a.getAnswerText())
                .collect(Collectors.toCollection(ArrayList::new));
        if(currentQuestionIndex + 1 < questions.size()) {
            buttonsText.add("След. Вопрос");
            buttonsCallback.add("EDIT TEST NEXT");
        }
        else {
            buttonsText.add("Завершить");
            buttonsCallback.add("TEST END");
        }
        userService.setCurrentQuestion(userId, currentQuestion);
        editMessageText.setReplyMarkup(keyboardService.createReply(buttonsText, buttonsCallback, ""));
        editMessageText.setText("Вопрос %s/%s: %s".formatted(currentQuestionIndex + 1, questions.size(), currentQuestion.getQuestion()));
    }
}