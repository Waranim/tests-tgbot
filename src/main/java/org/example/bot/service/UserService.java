package org.example.bot.service;

import org.example.bot.entity.TestEntity;
import org.example.bot.entity.UserEntity;
import org.example.bot.entity.UserContext;
import org.example.bot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

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
        Optional<UserEntity> user = getUserById(id);
        if (user.isPresent()) {
            return;
        }

        UserEntity userEntity = new UserEntity(id, username, new UserContext(id));
        userRepository.save(userEntity);
    }

    /**
     * Получить пользователя по идентификатору
     * @param id идентификатор пользователя
     * @return пользователь, или null, если пользователь не найден
     */
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Добавить получателю тест
     */
    public void addReceivedTest(Long recipientID, TestEntity test){
        Optional<UserEntity> recipient = getUserById(recipientID);
        recipient.get().getReceivedTests().add(test);
        userRepository.save(recipient.get());
    }

    /**
     * Получить открытые полученные тесты
     */
    public List<TestEntity> getOpenReceivedTests(Long userId){
        Optional<UserEntity> user = getUserById(userId);
        return user.get().getReceivedTests().stream().filter(TestEntity::isAccessOpen).toList();
    }

    /**
     * Удалить тест из полученных тестов
     */
    public void removeReceivedTest(Long userId, TestEntity test) {
        Optional<UserEntity> recipient = getUserById(userId);
        recipient.get().getReceivedTests().remove(test);
        userRepository.save(recipient.get());
    }
}
