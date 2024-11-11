package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.DTO.UserDTO;
import org.example.naumenteststgbot.entity.UserEntity;
import org.example.naumenteststgbot.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Взаимодействие с сущностью пользователя
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создание записи о пользователе в базе данных
     * @param id идентификатор телеграм
     * @param username псевдоним пользователя в телеграм
     */
    public void create(Long id, String username) {
        UserDTO user = get(id);
        if (user != null) {
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUsername(username);
        userRepository.save(userEntity);
    }

    /**
     * Получение пользователя из базы данных
     * @param id идентификатор телеграм
     * @return объект UserDTO, содержащий информацию о пользователе, или null, если пользователь не найден
     */
    public UserDTO get(Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        return new UserDTO(user.getId(), user.getUsername());
    }
}
