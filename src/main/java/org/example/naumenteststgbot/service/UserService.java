package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.UserEntity;
import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.enums.UserState;
import org.example.naumenteststgbot.repository.UserRepository;
import org.example.naumenteststgbot.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private final UserSessionRepository userSessionRepository;

    public UserService(UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
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
     * Получить текущую сессию пользователя
     * @param id идентификатор пользователя
     */
    public UserSession getSession(Long id) {
        UserEntity user = getUserById(id);
        if (user == null) return null;
        return user.getSession();
    }

    /**
     * Получить пользователя по идентификатору
     * @param id идентификатор пользователя
     * @return пользователь, или null, если пользователь не найден
     */
    private UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Сохранить обновленного пользователя и его сессию
     * @param user пользователь, которого нужно сохранить
     */
    private void updateUser(UserEntity user){
        userRepository.save(user);
        userSessionRepository.save(user.getSession());
    }

    /**
     * Изменить состояние сессии пользователя по идентификатору
     * @param id идентификатор пользователя
     * @param state состояние пользователя
     */
    @Transactional
    public void changeStateById(Long id, UserState state) {
        UserEntity user = getUserById(id);
        if (user == null) return;
        user.getSession().setState(state);
        updateUser(user);
    }

    /**
     * Установить текущий тест к сессии пользователя
     * @param id идентификатор пользователя
     * @param testEntity тест
     */
    @Transactional
    public void setCurrentTest(Long id, TestEntity testEntity) {
        UserEntity user = getUserById(id);
        if (user == null) return;
        user.getSession().setCurrentTest(testEntity);
        updateUser(user);
    }
    /**
     * Установить индекс правильного ответа
     * @param id идентификатор пользователя
     * @param editingAnswerIndex индекс редактируемого варианта ответа
     */
    @Transactional
    public void setEditingAnswerIndex(Long id, Integer editingAnswerIndex) {
        UserEntity user = getUserById(id);
        if (user == null) return;
        user.getSession().setEditingAnswerIndex(editingAnswerIndex);
        updateUser(user);
    }

    /**
     * Получить список тестов по идентификатору пользователя
     * @param id идентификатор пользователя
     * @return список тестов или null, если пользователь не найден
     */
    public List<TestEntity> getTestsById(Long id) {
        UserEntity user = getUserById(id);
        if (user == null) return null;
        return user.getTests();
    }

    public void setCurrentQuestion(Long userId, QuestionEntity question) {
        UserEntity user = getUserById(userId);
        if (user == null) return;
        user.getSession().setCurrentQuestion(question);
        updateUser(user);
    }

    public QuestionEntity getCurrentQuestion(Long userId) {
        UserEntity user = getUserById(userId);
        if (user == null) return null;
        return user.getSession().getCurrentQuestion();
    }
}
