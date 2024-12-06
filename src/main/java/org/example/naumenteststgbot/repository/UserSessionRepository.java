package org.example.naumenteststgbot.repository;

import org.example.naumenteststgbot.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия над сущностью сессии пользователя в базе данных
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
}
