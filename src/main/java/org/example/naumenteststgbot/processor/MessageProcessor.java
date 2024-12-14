package org.example.naumenteststgbot.processor;


/**
 * Обработчик сообщений
 */
public interface MessageProcessor {
    
    /**
     * Может ли обработать сообщение
     */
    boolean canProcess(Long userId, String message);

    /**
     * Обработать сообщение
     * @param userId идентификатор пользователя
     * @param message сообщение
     * @return Ответ на сообщение
     */
    String process(Long userId, String message);
}