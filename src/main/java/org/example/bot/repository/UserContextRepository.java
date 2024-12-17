package org.example.bot.repository;

import org.example.bot.entity.UserContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия над сущностью контекста пользователя в базе данных
 */
@Repository
public interface UserContextRepository extends JpaRepository<UserContext, Long> {
}
