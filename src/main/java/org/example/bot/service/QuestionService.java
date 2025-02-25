package org.example.bot.service;

import org.example.bot.entity.*;
import org.example.bot.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для создания, обновления, получения и удаления вопроса
 * А также для добавления ответа к вопросу и установки правильного ответа
 */
@Service
public class QuestionService {

    /**
     * Репозиторий для взаимодействия над сущностью вопросов в базе данных
     */
    private final QuestionRepository questionRepository;

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;


    /**
     * Конструктор для инициализации сервиса и репозитория
     */
    public QuestionService(QuestionRepository questionRepository, TestService testService) {
        this.questionRepository = questionRepository;
        this.testService = testService;
    }

    /**
     * Создает новый вопрос в заданном тесте и сохраняет его в базе данных
     */
    public QuestionEntity createQuestion(TestEntity test) {
        QuestionEntity question = new QuestionEntity(test);
        questionRepository.save(question);

        return question;
    }

    /**
     * Добавляет новый вариант ответа к текущему вопросу
     */
    public void addAnswerOption(QuestionEntity question, String answerText) {
        AnswerEntity newAnswer = new AnswerEntity(answerText);
        newAnswer.setQuestion(question);
        question.getAnswers().add(newAnswer);
        questionRepository.save(question);
    }

    /**
     * Устанавливает правильный ответ для вопроса.
     */
    public void setCorrectAnswer(QuestionEntity question, int optionIndex) {
        List<AnswerEntity> answers = question.getAnswers();
        if (optionIndex < 1 || optionIndex > answers.size()) {
            throw new IllegalArgumentException("Некорректный номер варианта ответа.");
        }
        for (int i = 0; i < answers.size(); i++) {
            answers.get(i).setCorrect(i == optionIndex - 1);
        }
        questionRepository.save(question);
    }

    /**
     * Обновить вопрос в базе данных
     * @param question вопрос, который нужно обновить
     */
    public void update(QuestionEntity question) {
        if (question != null)
            questionRepository.save(question);
    }

    /**
     * Удаление вопроса
     *
     * @param question вопрос, который нужно удалить
     */
    public void delete(QuestionEntity question) {
        TestEntity test = question.getTest();
        test.getQuestions().remove(question);
        testService.update(test);
        questionRepository.delete(question);
    }

    /**
     * Получить вопрос по идентификатору
     *
     * @param id идентификатор вопроса
     * @return Optional с вопросом, или пустой Optional, если не найден
     */
    public Optional<QuestionEntity> getQuestion(Long id) {
        return questionRepository.findById(id);
    }
}