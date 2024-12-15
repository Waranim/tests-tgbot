package org.example.bot.service;

import org.example.bot.entity.UserEntity;
import org.example.bot.entity.UserSession;
import org.example.bot.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Взаимодействие с сущностью пользователя
 */
@Service
public class UserService {

    /**
     * Репозиторий для взаимодействия с базой данных
     */
    private final UserRepository userRepository;

    /**
     * Взаимодействие с сущностью пользователя
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создание записи о пользователе в базе данных
     * @param id идентификатор телеграм
     * @param username псевдоним пользователя в телеграм
     */
    public void create(Long id, String username) {
        UserEntity user = getUserById(id);
        if (user != null) {
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(id);
        userEntity.setUsername(username);
        userEntity.setSession(new UserSession(id));
        userRepository.save(userEntity);
    }

    /**
     * Получить пользователя по идентификатору
     * @param id идентификатор пользователя
     * @return пользователь, или null, если пользователь не найден
     */
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
