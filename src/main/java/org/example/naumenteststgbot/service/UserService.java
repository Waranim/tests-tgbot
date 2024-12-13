package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.UserEntity;
import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Взаимодействие с сущностью пользователя
 */
@Service
@Transactional
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
        userEntity.setId(id);
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
