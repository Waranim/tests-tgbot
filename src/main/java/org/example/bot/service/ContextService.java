package org.example.bot.service;

import org.example.bot.entity.*;
import org.example.bot.repository.UserContextRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления контекстом пользователя
 */
@Service
public class ContextService {

    /**
     * Репозиторий контекста пользователя
     */
    private final UserContextRepository userContextRepository;

    /**
     * Сервис пользователя
     */
    private final UserService userService;

    /**
     * Конструктор для сервиса контекста пользователя
     * @param userContextRepository Репозиторий контекста пользователя
     * @param userService Сервис пользователя
     */
    public ContextService(UserContextRepository userContextRepository,
                          UserService userService) {
        this.userContextRepository = userContextRepository;
        this.userService = userService;
    }

    /**
     * Получить текущий контекст пользователя
     * @param userId идентификатор пользователя
     * @return контекст пользователя или null, если пользователь не найден
     */
    public UserContext getContext(Long userId) {
        UserEntity user = userService.getUserById(userId);
        if (user == null)
            return null;
        return user.getContext();
    }

    /**
     * Установить текущий тест в контексте пользователя
     * @param userId идентификатор пользователя
     * @param testEntity тест
     */
    public void setCurrentTest(Long userId, TestEntity testEntity) {
        UserContext context = getContext(userId);
        if (context == null)
            return;
        context.setCurrentTest(testEntity);
        userContextRepository.save(context);
    }

    /**
     * Установить текущий вопрос в контексте
     * @param userId идентификатор пользователя
     * @param question вопрос
     */
    public void setCurrentQuestion(Long userId, QuestionEntity question) {
        UserContext context = getContext(userId);
        if (context == null)
            return;
        context.setCurrentQuestion(question);
        userContextRepository.save(context);
    }

    /**
     * Получить текущий вопрос из контекста
     * @param userId идентификатор пользователя
     * @return текущий вопрос или null, если пользователь не найден
     */
    public QuestionEntity getCurrentQuestion(Long userId) {
        UserContext context = getContext(userId);
        if (context == null)
            return null;
        return context.getCurrentQuestion();
    }

    /**
     * Получить текущий тест из контекста
     * @param userId идентификатор пользователя
     * @return текущий тест или null, если пользователь не найден
     */
    public TestEntity getCurrentTest(Long userId) {
        UserContext context = getContext(userId);
        if (context == null)
            return null;
        return context.getCurrentTest();
    }

    /**
     * Установить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @param editingAnswerIndex индекс редактируемого ответа
     */
    public void setEditingAnswerIndex(Long userId, Integer editingAnswerIndex) {
        UserContext context = getContext(userId);
        if (context == null)
            return;
        context.setEditingAnswerIndex(editingAnswerIndex);
        userContextRepository.save(context);
    }

    /**
     * Получить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @return индекс редактируемого ответа или null, если пользователь не найден
     */
    public Integer getEditingAnswerIndex(Long userId) {
        UserContext context = getContext(userId);
        if (context == null)
            return null;
        return context.getEditingAnswerIndex();
    }

    /**
     * Обновление контекста пользователя в базе данных
     * @param context контекст пользователя
     */
    public void updateContext(UserContext context) {
        userContextRepository.save(context);
    }
}