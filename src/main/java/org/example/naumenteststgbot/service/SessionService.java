package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления сессией пользователя
 */
@Service
@Transactional
public class SessionService {

    /**
     * Репозиторий сессии пользователя
     */
    private final UserSessionRepository userSessionRepository;

    /**
     * Сервис пользователя
     */
    private final UserService userService;

    /**
     * Конструктор для сервиса сессии пользователя
     * @param userSessionRepository Репозиторий сессии пользователя
     * @param userService Сервис пользователя
     */
    public SessionService(UserSessionRepository userSessionRepository,
                         UserService userService) {
        this.userSessionRepository = userSessionRepository;
        this.userService = userService;
    }

    /**
     * Получить текущую сессию пользователя
     * @param userId идентификатор пользователя
     * @return сессия пользователя или null, если пользователь не найден
     */
    public UserSession getSession(Long userId) {
        UserEntity user = userService.getUserById(userId);
        if (user == null)
            return null;
        return user.getSession();
    }

    /**
     * Установить текущий тест в сессии пользователя
     * @param userId идентификатор пользователя
     * @param testEntity тест
     */
    public void setCurrentTest(Long userId, TestEntity testEntity) {
        UserSession session = getSession(userId);
        if (session == null)
            return;
        session.setCurrentTest(testEntity);
        userSessionRepository.save(session);
    }

    /**
     * Установить текущий вопрос в сессии
     * @param userId идентификатор пользователя
     * @param question вопрос
     */
    public void setCurrentQuestion(Long userId, QuestionEntity question) {
        UserSession session = getSession(userId);
        if (session == null)
            return;
        session.setCurrentQuestion(question);
        userSessionRepository.save(session);
    }

    /**
     * Получить текущий вопрос из сессии
     * @param userId идентификатор пользователя
     * @return текущий вопрос или null, если пользователь не найден
     */
    public QuestionEntity getCurrentQuestion(Long userId) {
        UserSession session = getSession(userId);
        if (session == null)
            return null;
        return session.getCurrentQuestion();
    }

    /**
     * Получить текущий тест из сессии
     * @param userId идентификатор пользователя
     * @return текущий тест или null, если пользователь не найден
     */
    public TestEntity getCurrentTest(Long userId) {
        UserSession session = getSession(userId);
        if (session == null) return null;
        return session.getCurrentTest();
    }

    /**
     * Установить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @param editingAnswerIndex индекс редактируемого ответа
     */
    public void setEditingAnswerIndex(Long userId, Integer editingAnswerIndex) {
        UserSession session = getSession(userId);
        if (session == null)
            return;
        session.setEditingAnswerIndex(editingAnswerIndex);
        userSessionRepository.save(session);
    }

    /**
     * Получить индекс редактируемого ответа
     * @param userId идентификатор пользователя
     * @return индекс редактируемого ответа или null, если пользователь не найден
     */
    public Integer getEditingAnswerIndex(Long userId) {
        UserSession session = getSession(userId);
        if (session == null) return null;
        return session.getEditingAnswerIndex();
    }

    /**
     * Обновление сессии пользователя в базе данных
     * @param session сессия пользователя
     */
    public void updateSession(UserSession session) {
        userSessionRepository.save(session);
    }
}