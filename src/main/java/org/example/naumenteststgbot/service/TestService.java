package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.*;
import org.example.naumenteststgbot.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для создания, обновления, получения и удаления теста
 */
@Service
public class TestService {
    /**
     * Репозиторий для тестов
     */
    private final TestRepository testRepository;

    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Конструктор сервиса тестов
     * @param testRepository Репозиторий для тестов
     * @param userService Сервис пользователей
     */
    public TestService(TestRepository testRepository, UserService userService) {
        this.testRepository = testRepository;
        this.userService = userService;
    }

    /**
     * Получить тест по идентификатору
     * @param id Идентификатор теста
     * @return тест или null, если не найден
     */
    public TestEntity getTest(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    /**
     * Создать тест
     * @param creatorId Идентификатор создателя
     * @return Созданный тесть
     */
    public TestEntity createTest(Long creatorId){
        TestEntity test = new TestEntity(creatorId);
        return testRepository.save(test);
    }

    /**
     * Получить список тестов по идентификатору пользователя
     * @param userId идентификатор пользователя
     * @return список тестов или null, если пользователь не найден
     */
    public List<TestEntity> getTestsById(Long userId) {
        UserEntity user = userService.getUserById(userId);
        if (user == null) return null;
        return user.getTests();
    }

    /**
     * Удаление теста
     * @param test тест, который нужно удалить
     */
    public void delete(TestEntity test) {
        testRepository.delete(test);
    }

    /**
     * Обновить тест в базе данных
     * @param test тест, который нужно обновить
     */
    public void update(TestEntity test) {
        if(test != null)
            testRepository.save(test);
    }
}