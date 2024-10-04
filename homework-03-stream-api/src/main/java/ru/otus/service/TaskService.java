package ru.otus.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.otus.data.Task;
import ru.otus.data.TaskStatus;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class TaskService {

    private final List<Task> tasks;

    public List<Task> findAllByStatus(@NonNull TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    public boolean taskByIdExists(@NonNull Integer id) {
        return tasks.stream()
                .anyMatch(task -> task.getId().equals(id));
    }

    public List<Task> findAllOrderByStatus() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getStatus))
                .toList();
    }

    public Long getCountByStatus(@NonNull TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }
}
