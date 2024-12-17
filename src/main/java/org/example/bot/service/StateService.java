package org.example.bot.service;

import org.example.bot.entity.UserContext;
import org.example.bot.state.UserState;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления контекстом пользователя
 */
@Service
public class StateService {

    /**
     * Сервис контекста пользователя
     */
    private final ContextService contextService;

    /**
     * Конструктор сервиса состояний
     * @param contextService Сервис контекста
     */
    public StateService(ContextService contextService) {
        this.contextService = contextService;
    }

    /**
     * Изменить состояние контекста пользователя по идентификатору
     * @param id идентификатор пользователя
     * @param state состояние пользователя
     */
    public void changeStateById(Long id, UserState state) {
        UserContext context = contextService.getContext(id);
        if (context == null)
            return;
        context.setState(state);
        contextService.updateContext(context);
    }

    /**
     * Получить текущее состояние пользователя
     * @param id идентификатор пользователя
     * @return текущее состояние или null, если пользователь не найден
     */
    public UserState getCurrentState(Long id) {
        UserContext context = contextService.getContext(id);
        return context != null
                ? context.getState()
                : null;
    }
}
