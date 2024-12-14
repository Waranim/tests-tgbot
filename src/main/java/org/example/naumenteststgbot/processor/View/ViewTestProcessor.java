package org.example.naumenteststgbot.processor.View;

import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния просмотра тестов.
 */
@Component
public class ViewTestProcessor extends AbstractStateProcessor {
    /**
     * Обработчик команды просмотра тестов.
     */
    private final ViewCommandProcessor viewCommandProcessor;

    /**
     * Конструктор для инициализации обработчика состояния просмотра тестов.
     * 
     * @param stateService сервис для управления состояниями
     * @param viewCommandProcessor обработчик команды просмотра тестов
     */
    public ViewTestProcessor(StateService stateService,
                             ViewCommandProcessor viewCommandProcessor) {
        super(stateService, UserState.VIEW_TEST);
        this.viewCommandProcessor = viewCommandProcessor;
    }

    @Override
    public String process(Long userId, String message) {
        return viewCommandProcessor.process(userId, "/view " + message);
    }
}
