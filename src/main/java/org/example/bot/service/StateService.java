package org.example.bot.service;

import org.example.bot.entity.UserSession;
import org.example.bot.states.UserState;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления состоянием пользователя
 */
@Service
public class StateService {

    /**
     * Сервис сессии
     */
    private final SessionService sessionService;

    /**
     * Конструктор сервиса состояний
     * @param sessionService Сервис сессии
     */
    public StateService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Изменить состояние сессии пользователя по идентификатору
     * @param id идентификатор пользователя
     * @param state состояние пользователя
     */
    public void changeStateById(Long id, UserState state) {
        UserSession session = sessionService.getSession(id);
        if (session == null) return;
        session.setState(state);
        sessionService.updateSession(session);
    }

    /**
     * Получить текущее состояние пользователя
     * @param id идентификатор пользователя
     * @return текущее состояние или null, если пользователь не найден
     */
    public UserState getCurrentState(Long id) {
        UserSession session = sessionService.getSession(id);
        return session != null
                ? session.getState()
                : null;
    }
}
