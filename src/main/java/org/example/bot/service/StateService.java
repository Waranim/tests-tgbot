package org.example.bot.service;

import org.example.bot.entity.UserContext;
import org.example.bot.state.UserState;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<UserContext> contextOpt = contextService.getContext(id);
        contextOpt.ifPresent(context -> {
            context.setState(state);
            contextService.updateContext(context);
        });
    }

    /**
     * Получить текущее состояние пользователя
     * @param id идентификатор пользователя
     * @return текущее состояние пользователя, если контекст существует, иначе пустой Optional
     */
    public Optional<UserState> getCurrentState(Long id) {
        return contextService.getContext(id)
                .map(UserContext::getState);
    }
}
