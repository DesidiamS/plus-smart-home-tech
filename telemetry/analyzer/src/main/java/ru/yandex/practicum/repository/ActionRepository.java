package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.domain.Action;
import ru.yandex.practicum.domain.Scenario;

import java.util.Collection;
import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    void deleteByScenario(Scenario scenario);

    List<Action> findAllByScenarioIn(Collection<Scenario> scenarios);
}