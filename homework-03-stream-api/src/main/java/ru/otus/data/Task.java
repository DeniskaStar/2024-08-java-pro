package ru.otus.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {

    private Integer id;
    private String name;
    private TaskStatus status;
}
