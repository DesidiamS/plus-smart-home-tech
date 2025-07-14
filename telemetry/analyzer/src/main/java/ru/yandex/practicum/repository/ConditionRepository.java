package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.Condition;
import ru.yandex.practicum.domain.Scenario;

import java.util.Collection;
import java.util.List;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    void deleteByScenario(Scenario scenario);

    List<Condition> findByScenarioIn(Collection<Scenario> scenarios);
}