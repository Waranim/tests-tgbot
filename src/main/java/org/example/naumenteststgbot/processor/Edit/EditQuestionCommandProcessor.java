package org.example.naumenteststgbot.processor.Edit;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды редактирования вопрса
 */
@Component
public class EditQuestionCommandProcessor extends AbstractCommandProcessor {

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор для инициализации обработчика команды редактирования вопроса
     *
     * @param stateService    cервис для управления состояниями
     * @param sessionService  cервис для управления сессиями
     * @param questionService cервис для управления вопросами
     */
    public EditQuestionCommandProcessor(StateService stateService,
                                        SessionService sessionService,
                                        QuestionService questionService) {
        super("/edit_question");
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.questionService = questionService;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return "Используйте команду вместе с идентификатором вопроса!";
        }
        if (parts[1].matches("^\\d+$")) {
            Long questionId = Long.parseLong(parts[1]);
            QuestionEntity question = questionService.getQuestion(questionId);
            if (question == null) {
                return "Вопрос не найден!";
            }
            sessionService.setCurrentQuestion(userId, question);
            stateService.changeStateById(userId, UserState.EDIT_QUESTION);
            return String.format("""
                    Вы выбрали вопрос “%s”. Что вы хотите изменить в вопросе?
                    1: Формулировку вопроса
                    2: Варианты ответа
                    """, question.getQuestion());
        }
        return "Ошибка ввода. Укажите корректный id теста.";
    }
}
