package org.example.bot.processor;

import org.example.bot.service.StateService;
import org.example.bot.state.UserState;

import java.util.Optional;

/**
 * Абстрактный класс для обработки состояний.
 */
public abstract class AbstractStateProcessor implements MessageProcessor {
    /**
     * Сервис для управления состояниями.
     */
    protected final StateService stateService;
    
    /**
     * Необходимое состояние для обработки.
     */
    private final UserState requiredState;

    /**
     * Конструктор для инициализации обработчика состояния.
     * 
     * @param stateService сервис для управления состояниями
     * @param requiredState необходимое состояние для обработки
     */
    protected AbstractStateProcessor(StateService stateService,
                                     UserState requiredState) {
        this.stateService = stateService;
        this.requiredState = requiredState;
    }

    @Override
    public final boolean canProcess(Long userId, String message) {
        Optional<UserState> stateOpt = stateService.getCurrentState(userId);
        return stateOpt.map(state -> state.equals(requiredState)).orElse(false);
    }
}