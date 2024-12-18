package org.example.bot.service;

import org.example.bot.entity.*;
import org.example.bot.repository.UserContextRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
     * Получить контекст пользователя
     * @param userId идентификатор пользователя
     * @return контекст пользователя в Optional, пустой Optional если пользователь не найден
     */
    public Optional<UserContext> getContext(Long userId) {
        return Optional.ofNullable(userService.getUserById(userId))
                .map(UserEntity::getContext);
    }

    /**
     * Установить текущий тест в контексте пользователя
     * @param userId идентификатор пользователя
     * @param testEntity тест
     */
    public void setCurrentTest(Long userId, TestEntity testEntity) {
        getContext(userId).ifPresent(context -> {
            context.setCurrentTest(testEntity);
            userContextRepository.save(context);
        });
    }

    /**
     * Установить текущий вопрос в контексте
     * @param userId идентификатор пользователя
     * @param question вопрос
     */
    public void setCurrentQuestion(Long userId, QuestionEntity question) {
        getContext(userId).ifPresent(context -> {
            context.setCurrentQuestion(question);
            userContextRepository.save(context);
        });
    }

    /**
     * Получить текущий вопрос из контекста
     * @param userId идентификатор пользователя
     * @return текущий вопрос в Optional
     */
    public Optional<QuestionEntity> getCurrentQuestion(Long userId) {
        return getContext(userId).map(UserContext::getCurrentQuestion);
    }

    /**
     * Получить текущий тест из контекста
     * @param userId идентификатор пользователя
     * @return текущий тест в Optional
     */
    public Optional<TestEntity> getCurrentTest(Long userId) {
        return getContext(userId).map(UserContext::getCurrentTest);
    }

    /**
     * Установить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @param editingAnswerIndex индекс редактируемого ответа
     */
    public void setEditingAnswerIndex(Long userId, Integer editingAnswerIndex) {
        getContext(userId).ifPresent(context -> {
            context.setEditingAnswerIndex(editingAnswerIndex);
            userContextRepository.save(context);
        });
    }

    /**
     * Получить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @return индекс редактируемого ответа в Optional
     */
    public Optional<Integer> getEditingAnswerIndex(Long userId) {
        return getContext(userId).map(UserContext::getEditingAnswerIndex);
    }

    /**
     * Обновление контекста пользователя в базе данных
     * @param context контекст пользователя
     */
    public void updateContext(UserContext context) {
        userContextRepository.save(context);
    }
}