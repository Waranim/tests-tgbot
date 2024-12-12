package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.UserEntity;
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
    private final SessionService sessionService;

    public UserService(UserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
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
        userEntity.setSession(sessionService.createSession(id));
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
