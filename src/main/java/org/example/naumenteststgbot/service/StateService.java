package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления состоянием пользователя
 */
@Service
@Transactional
public class StateService {

    private final UserSessionRepository userSessionRepository;
    private final SessionService sessionService;

    public StateService(UserSessionRepository userSessionRepository,
                        SessionService sessionService) {
        this.userSessionRepository = userSessionRepository;
        this.sessionService = sessionService;
    }

    /**
     * Изменить состояние сессии пользователя по идентификатору
     * @param id идентификатор пользователя
     * @param state состояние пользователя
     */
    @Transactional
    public void changeStateById(Long id, UserState state) {
        UserSession session = sessionService.getSession(id);
        if (session == null) return;
        session.setState(state);
        userSessionRepository.save(session);
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
