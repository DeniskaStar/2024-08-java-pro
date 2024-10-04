package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.data.Task;
import ru.otus.data.TaskStatus;
import ru.otus.service.TaskService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskServiceTest {

    private static final Integer TASK_EXIST_ID = 1;
    private static final Integer TASK_NOT_EXIST_ID = 500;
    private static final Long COUNT_TASKS_IN_CLOSED_STATUS = 3L;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(1, "Task 1", TaskStatus.OPEN));
        taskList.add(new Task(2, "Task 2", TaskStatus.IN_PROGRESS));
        taskList.add(new Task(3, "Task 3", TaskStatus.IN_PROGRESS));
        taskList.add(new Task(4, "Task 4", TaskStatus.CLOSED));
        taskList.add(new Task(5, "Task 5", TaskStatus.CLOSED));
        taskList.add(new Task(6, "Task 6", TaskStatus.CLOSED));

        taskService = new TaskService(taskList);
    }

    @Test
    @DisplayName("Поиск всех задач по статусу")
    void findAllByStatus() {
        List<Task> openTasks = taskService.findAllByStatus(TaskStatus.OPEN);
        List<Task> inProgressTasks = taskService.findAllByStatus(TaskStatus.IN_PROGRESS);
        List<Task> closedTasks = taskService.findAllByStatus(TaskStatus.CLOSED);

        assertThat(openTasks).hasSize(1);
        assertThat(inProgressTasks).hasSize(2);
        assertThat(closedTasks).hasSize(3);
    }

    @Test
    @DisplayName("Поиск задачи по существующему id")
    void taskByIdExists_whenTaskByIdExists_shouldReturnTrue() {
        boolean taskExists = taskService.taskByIdExists(TASK_EXIST_ID);

        assertThat(taskExists).isTrue();
    }

    @Test
    @DisplayName("Поиск задачи по несуществующему id")
    void taskByIdExists_whenTaskByIdNotExists_shouldReturnFalse() {
        boolean taskExists = taskService.taskByIdExists(TASK_NOT_EXIST_ID);

        assertThat(taskExists).isFalse();
    }

    @Test
    @DisplayName("Поиск задач, отсортированных по статусу")
    void findAllOrderByStatus() {
        List<Task> actualTasks = taskService.findAllOrderByStatus();

        assertThat(actualTasks)
                .isSortedAccordingTo(Comparator.comparing(Task::getStatus));
    }

    @Test
    @DisplayName("Количество задач в определенном статусе")
    void getCountByStatus() {
        Long countTasks = taskService.getCountByStatus(TaskStatus.CLOSED);

        assertThat(countTasks).isEqualTo(COUNT_TASKS_IN_CLOSED_STATUS);
    }
}
