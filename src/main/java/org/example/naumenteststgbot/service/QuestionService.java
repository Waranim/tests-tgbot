package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.enums.UserState;
import org.example.naumenteststgbot.repository.AnswerRepository;
import org.example.naumenteststgbot.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.ArrayList;

import java.util.List;

/**
 * Класс для управления вопросами
 * Предостволяет функционал добавления, редактирования, просмотра и удаления вопросов
 */
@Service
@Transactional
public class QuestionService {
    /**
     * Репозиторий для взаимодействия над сущностью вопросов в базе данных
     */
    private final QuestionRepository questionRepository;

    /**
     * Сервис для взаимодействия с пользователем
     */
    private final UserService userService;

    /**
     * Репозиторий для взаимодействия над сущностью ответов в базе данных
     */
    private final AnswerRepository answerRepository;

    /**
     * Сервис для взаимодействия с тестами
     */
    private final TestService testService;

    /**
     * Сервис для работы с inline клавиатурой
     */
    private final KeyboardService keyboardService;

    /**
     * Создание сообщений
     */
    private final MessageBuilder messageBuilder;

    /**
     * Конструктор для инициализации сервисов и репозиториев
     */
    public QuestionService(QuestionRepository questionRepository, UserService userService, AnswerRepository answerRepository, TestService testService, KeyboardService keyboardService, MessageBuilder messageBuilder) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.answerRepository = answerRepository;
        this.testService = testService;
        this.keyboardService = keyboardService;
        this.messageBuilder = messageBuilder;
    }

    /**
     * Управляет переходами между состояниями пользователя в зависимости от полученной команды
     */
    public SendMessage handleMessage(String chatId, UserSession userSession, String text) {
        UserState userState = userSession.getState();
        Long userId = userSession.getUserId();
        QuestionEntity currentQuestion = userSession.getCurrentQuestion();
        String response = "Ошибка";

        switch (userState) {
            case DEFAULT:
                break;
            case ADD_QUESTION:
                if (!text.matches("^\\d+$")) {
                    return createSimpleMessage(chatId,"Некорректный id теста. Пожалуйста, введите число.");
                }

                long testId = Long.parseLong(text);
                TestEntity selectedTest = testService.getTest(testId);
                if (selectedTest == null || !userService.getTestsById(userId).contains(selectedTest)) {
                    return createSimpleMessage(chatId,"Тест не найден!");
                }
                QuestionEntity nquestion = createQuestion(selectedTest);
                userService.setCurrentQuestion(userId, nquestion);
                userService.setState(userId, UserState.ADD_QUESTION_TEXT);
                return createSimpleMessage(chatId,"Введите название вопроса для теста “%s”".formatted(selectedTest.getTitle()));


            case ADD_QUESTION_TEXT:
                currentQuestion.setQuestion(text);
                questionRepository.save(currentQuestion);
                response = "Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду /stop.";
                userService.changeStateById(userId, UserState.ADD_ANSWER);
                break;

            case ADD_ANSWER:
                addAnswerOption(currentQuestion, text);
                response = "Введите вариант ответа. Если вы хотите закончить добавлять варианты, введите команду “/stop”.";
                break;

            case EDIT_QUESTION:
                if (text.equals("1")) {
                    response = "Введите новый текст вопроса";
                    userService.changeStateById(userId, UserState.EDIT_QUESTION_TEXT);
                } else if (text.equals("2")) {
                    response = "Что вы хотите сделать с вариантом ответа?\n1: Изменить формулировку ответа\n2: Изменить правильность варианта ответа";
                    userService.changeStateById(userId, UserState.EDIT_ANSWER_OPTION_CHOICE);
                }
                break;

            case EDIT_QUESTION_TEXT:
                currentQuestion.setQuestion(text);
                questionRepository.save(currentQuestion);
                response = String.format("Текст вопроса изменен на “%s”", text);
                userService.changeStateById(userId, UserState.DEFAULT);
                break;

            case EDIT_ANSWER_OPTION_CHOICE:
                if (text.equals("1")) {
                    response = "Сейчас варианты ответа выглядят так\n" + answersListToString(currentQuestion.getAnswers());
                    response += "\nКакой вариант ответа вы хотите изменить?";
                    userService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT_CHOICE);
                } else if (text.equals("2")) {
                    response = "Сейчас варианты ответа выглядят так:\n" + answersListToString(currentQuestion.getAnswers());
                    response += "\nКакой вариант ответа вы хотите сделать правильным?";
                    userService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
                }
                break;

            case EDIT_ANSWER_TEXT_CHOICE:
                int answerIndex = Integer.parseInt(text) - 1;
                if (answerIndex < 0 || answerIndex >= currentQuestion.getAnswers().size()) {
                    response = "Некорректный номер ответа. Попробуйте еще раз.";
                } else {
                    userService.setEditingAnswerIndex(userId, answerIndex);
                    response = "Введите новую формулировку ответа";
                    userService.changeStateById(userId, UserState.EDIT_ANSWER_TEXT);
                }
                break;

            case EDIT_ANSWER_TEXT:
                int editingAnswerIndex = userSession.getEditingAnswerIndex();
                currentQuestion.getAnswers().get(editingAnswerIndex).setAnswerText(text);
                questionRepository.save(currentQuestion);
                response = String.format("Формулировка изменена на “%s”", text);
                userService.changeStateById(userId, UserState.DEFAULT);
                break;

            case SET_CORRECT_ANSWER:
                String setCorrectAnswer = setCorrectAnswer(currentQuestion, Integer.parseInt(text));
                if (setCorrectAnswer.startsWith("Некорректный")) {
                    response = setCorrectAnswer;
                } else {
                    response = setCorrectAnswer;
                    userService.changeStateById(userId, UserState.DEFAULT);
                }
                break;

            case DELETE_QUESTION:
                QuestionEntity question = questionRepository.findById(Long.parseLong(text)).orElse(null);
                if (question == null) return messageBuilder.createSendMessage(chatId,"Вопрос не найден",null);
                userService.setCurrentQuestion(userId, question);
                userService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
                List<String> buttonsText = List.of("Да", "Нет");
                List<String> callback = List.of("confirmDeleteYes", "confirmDeleteNo");
                return messageBuilder.createSendMessage(chatId, "Вы уверены, что хотите удалить вопрос “%s”?".formatted(question.getQuestion()), keyboardService.createReply(buttonsText, callback, "QUESTION")
                );

            case CONFIRM_DELETE_QUESTION:
                text = text.toLowerCase();
                if (text.equals("да")) {
                    userService.setCurrentQuestion(userId, null);
                    questionRepository.delete(currentQuestion);
                    response = String.format("Вопрос “%s” из теста “%s” удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
                } else {
                    response = String.format("Вопрос “%s” из теста “%s” не удален.", currentQuestion.getQuestion(), currentQuestion.getTest().getTitle());
                }
                userService.changeStateById(userId, UserState.DEFAULT);
                break;
        }

        return createSimpleMessage(chatId,response);
    }

    /**
     * Обрабатывает команду добавления вопроса в тест
     */
    public String handleAddQuestion(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = userService.getTestsById(userId);

        if (parts.length == 1) {
            if (tests.isEmpty()) {
                return "У вас нет доступных тестов для добавления вопросов.";
            }
            userService.changeStateById(userId, UserState.ADD_QUESTION);
            return "Выберите тест:\n" + testsListToString(tests);
        }

        String testIdStr = parts[1];
        if (!testIdStr.matches("^-?\\d+$")) {
            return "Некорректный формат id теста. Пожалуйста, введите число.";
        }
        long testId = Long.parseLong(testIdStr);
        TestEntity test = testService.getTest(testId);
        if (test == null || !tests.contains(test)) {
            return "Тест не найден!";
        }

        QuestionEntity question = createQuestion(test);
        userService.changeStateById(userId, UserState.ADD_QUESTION_TEXT);
        userService.setCurrentQuestion(userId, question);
        return String.format("Введите название вопроса для теста “%s”", test.getTitle());
    }

    /**
     * Обрабатывает команду просмотра вопросов в тесте
     */
    public String handleViewQuestions(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return "Используйте команду вместе с идентификатором вопроса!";
        }
        if (parts[1].matches("^\\d+$")) {
            Long testId = Long.parseLong(parts[1]);
            TestEntity test = testService.getTest(testId);
            if (test == null || !test.getCreatorId().equals(userId)) {
                return "Тест не найден!";
            }
            List<QuestionEntity> questions = test.getQuestions();
            if (questions.isEmpty()) {
                return "В этом тесте пока нет вопросов.";
            }
            StringBuilder response = new StringBuilder();
            response.append(String.format("Вопросы теста \"%s\":\n", test.getTitle()));
            for (int i = 0; i < questions.size(); i++) {
                QuestionEntity question = questions.get(i);
                response.append(String.format("%d) id:%d  \"%s\"\n", i + 1, question.getId(), question.getQuestion()));
            }
            return response.toString();
        }

        return "Ошибка ввода. Укажите корректный id теста.";
    }

    /**
     * Обрабатывает команду редактирования вопроса
     */
    public SendMessage handleEditQuestion(String chatId,Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return createSimpleMessage(chatId,"Используйте команду вместе с идентификатором вопроса!");
        }
        Long questionId = Long.parseLong(parts[1]);
        QuestionEntity question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return createSimpleMessage(chatId,"Вопрос не найден!");
        }
        userService.setCurrentQuestion(userId, question);
        userService.changeStateById(userId, UserState.EDIT_QUESTION);
        List<String> buttonsText = List.of("Формулировку вопроса","Варианты ответа");
        List<String> callback = List.of("changeText", "changeAnswer");
        return messageBuilder.createSendMessage(chatId,"Что вы хотите изменить в вопросе “%s” ".formatted(question.getQuestion()),keyboardService.createReply(buttonsText,callback,"QUESTION"));
    }

    /**
     * Обрабатывает команду удаления вопроса
     */
    public SendMessage handleDeleteQuestion(String chatId, Long userId, String message) {
        UserSession userSession = userService.getSession(userId);
        if (userSession.getState() == UserState.CONFIRM_DELETE_QUESTION) {
            QuestionEntity question = userSession.getCurrentQuestion();
            if (question == null) {
                return createSimpleMessage(chatId, "Вопрос не найден!");
            }
            List<String> buttonsText = List.of("Да", "Нет");
            List<String> callback = List.of("confirmDeleteYes", "confirmDeleteNo");
            return messageBuilder.createSendMessage(chatId, "Вы уверены, что хотите удалить вопрос “%s”?".formatted(question.getQuestion()), keyboardService.createReply(buttonsText, callback, "QUESTION"));
        }
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String questionIdStr = parts[1];
            if (!questionIdStr.matches("^\\d+$")) {
                return createSimpleMessage(chatId, "Некорректный формат id вопроса. Пожалуйста, введите число.");
            }
            Long questionId = Long.parseLong(questionIdStr);
            QuestionEntity question = questionRepository.findById(questionId).orElse(null);
            if (question == null) {
                return createSimpleMessage(chatId, "Вопрос не найден!");

            }
            userService.setCurrentQuestion(userId, question);
            userService.changeStateById(userId, UserState.CONFIRM_DELETE_QUESTION);
            List<String> buttonsText = List.of("Да", "Нет");
            List<String> callback = List.of("confirmDeleteYes", "confirmDeleteNo");
            return messageBuilder.createSendMessage(chatId, "Вы уверены, что хотите удалить вопрос “%s”?".formatted(question.getQuestion()), keyboardService.createReply(buttonsText, callback, "QUESTION")
            );
        }
        if (parts.length == 1){
                userService.changeStateById(userId, UserState.DELETE_QUESTION);
                return createSimpleMessage(chatId, "Введите id вопроса для удаления:\n");
        }
        return createSimpleMessage(chatId, "Некорректный ввод команды");
    }

    /**
     * Завершает добавление вариантов ответа и переходит к выбору правильного ответа
     */
    public String handleStop(Long userId) {
        UserState userState = userService.getSession(userId).getState();
        QuestionEntity currentQuestion = userService.getCurrentQuestion(userId);
        if (currentQuestion == null) {
            return "Нет текущего вопроса. Пожалуйста, выберите или создайте вопрос.";
        }
        List<AnswerEntity> answers = currentQuestion.getAnswers();
        if (userState == UserState.ADD_ANSWER) {
            if (answers.size() < 2) {
                return "Вы не создали необходимый минимум ответов (минимум: 2). Введите варианты ответа.";
            }
            userService.changeStateById(userId, UserState.SET_CORRECT_ANSWER);
            return "Укажите правильный вариант ответа:\n" + answersListToString(currentQuestion.getAnswers());
        }
        return "Команда /stop используется только при создании вопроса";
    }

    /**
     * Создает новый вопрос в заданном тесте и сохраняет его в базе данных
     */
    private QuestionEntity createQuestion(TestEntity test) {
        QuestionEntity question = new QuestionEntity(test);
        questionRepository.save(question);

        return question;
    }

    /**
     * Добавляет новый вариант ответа к текущему вопросу
     */
    private void addAnswerOption(QuestionEntity question, String answerText) {
        AnswerEntity newAnswer = new AnswerEntity(answerText);
        newAnswer.setQuestion(question);
        answerRepository.save(newAnswer);
        question.getAnswers().add(newAnswer);
    }

    /**
     * Устанавливает правильный ответ для вопроса.
     */
    private String setCorrectAnswer(QuestionEntity question, int optionIndex) {
        List<AnswerEntity> answers = question.getAnswers();
        if (optionIndex < 1 || optionIndex > answers.size()) {
            return "Некорректный номер варианта ответа. Введите число от 1 до " + answers.size();
        }
        for (int i = 0; i < answers.size(); i++) {
            answers.get(i).setCorrect(i == optionIndex - 1);
        }
        questionRepository.save(question);
        return String.format("Вариант ответа %s назначен правильным.", optionIndex);
    }

    /**
     * Преобразует список ответов у вопроса в строку
     */
    private String answersListToString(List<AnswerEntity> answers) {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < answers.size(); i++) {
            AnswerEntity answer = answers.get(i);
            response.append(String.format("%d: %s%s\n", i + 1, answer.getAnswerText(), answer.isCorrect() ? " (верный)" : ""));
        }
        return response.toString();
    }

    /**
     * Преобразует список тестов в строку
     */
    private String testsListToString(List<TestEntity> tests) {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < tests.size(); i++) {
            TestEntity test = tests.get(i);
            response.append(String.format("%d) id: %d Название: %s\n", i + 1, test.getId(), test.getTitle()));
        }
        return response.toString();
    }

    /**
     * Обработать Callback query связанный с тестами
     */
    public SendMessage handleCallback(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();
        String[] callbackDataParts = callbackData.split(" ");
        if (callbackDataParts.length < 2) {
            return messageBuilder.createErrorMessage(chatId, "Некорректные данные в callback.");
        }

        Long userId = update.getCallbackQuery().getFrom().getId();
        String command = callbackDataParts[1];
        switch (command) {
            case "changeText":
                userService.setState(userId, UserState.EDIT_QUESTION_TEXT);
                return createSimpleMessage(chatId,
                        "Сейчас, формулировка к заданию выглядит так: %s\nВведите новую формулировку\n"
                                .formatted(userService.getCurrentQuestion(userId).getQuestion()));

            case "changeAnswer":
                return createChoiceMessage(chatId,
                        "Что вы хотите сделать?",
                        List.of("Изменить формулировку ответа", "Правильность варианта ответа"),
                        List.of("changeTextAnswerOption", "changeCorrectAnswerOption"));

            case "changeTextAnswerOption":
                return createAnswerOptionsMessage(chatId,
                        "Какой вариант ответа вы хотите изменить?\n",
                        userService.getCurrentQuestion(userId).getAnswers(),
                        "changeTextAnswer");

            case "changeTextAnswer":
                userService.setEditingAnswerIndex(userId, Integer.valueOf(callbackDataParts[2]));
                userService.setState(userId, UserState.EDIT_ANSWER_TEXT);
                return createSimpleMessage(chatId, "Введите новую формулировку");

            case "changeCorrectAnswerOption":
                return createAnswerOptionsMessage(chatId,
                        "Какой вариант ответа вы хотите изменить?\n",
                        userService.getCurrentQuestion(userId).getAnswers(),
                        "changeCorrectAnswer");

            case "changeCorrectAnswer":
                int index = Integer.parseInt(callbackDataParts[2]);
                setCorrectAnswer(userService.getCurrentQuestion(userId), index+1);
                userService.setState(userId, UserState.DEFAULT);
                return createSimpleMessage(chatId,
                        "Правильный вариант ответа “%s” был установлен"
                                .formatted(userService.getCurrentQuestion(userId).getAnswers().get(index).getAnswerText()));

            case "confirmDeleteYes":
                QuestionEntity questionToDelete = userService.getCurrentQuestion(userId);
                userService.setCurrentQuestion(userId, null);
                questionRepository.delete(questionToDelete);
                userService.setState(userId, UserState.DEFAULT);
                return createSimpleMessage(chatId, String.format("Вопрос “%s” успешно удалён.", questionToDelete.getQuestion()));

            case "confirmDeleteNo":
                userService.setState(userId, UserState.DEFAULT);
                return createSimpleMessage(chatId, "Удаление вопроса отменено.");

            default:
                return messageBuilder.createErrorMessage(chatId, "Неизвестная команда.");
        }

    }

    /**
     * Создает простое сообщение без кнопок и дополнительных элементов
     */
    private SendMessage createSimpleMessage(String chatId, String text) {
        return messageBuilder.createSendMessage(chatId, text, null);
    }

    /**
     * Создает сообщение с inline-кнопками для взаимодействия с пользователем
     */
    private SendMessage createChoiceMessage(String chatId, String text, List<String> buttonTexts, List<String> callback) {
        return messageBuilder.createSendMessage(chatId, text,
                keyboardService.createReply(buttonTexts, callback, "QUESTION"));
    }

    /**
     *  Создает сообщение с вариантами ответов в виде inline-кнопок
     */
    private SendMessage createAnswerOptionsMessage(String chatId, String text, List<AnswerEntity> answers, String callbackPrefix) {
        List<String> buttonTexts = new ArrayList<>();
        List<String> callback = new ArrayList<>();

        for (int i = 0; i < answers.size(); i++) {
            String postfix = answers.get(i).isCorrect() ? " (верный)" : "";
            buttonTexts.add(answers.get(i).getAnswerText() + postfix);
            callback.add(callbackPrefix + " " + i);
        }

        return messageBuilder.createSendMessage(chatId, text,
                keyboardService.createReply(buttonTexts, callback, "QUESTION"));
    }
}