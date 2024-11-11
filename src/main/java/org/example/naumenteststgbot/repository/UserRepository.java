package org.example.naumenteststgbot.repository;

import org.example.naumenteststgbot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия над сущностью пользователя в базе данных
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
