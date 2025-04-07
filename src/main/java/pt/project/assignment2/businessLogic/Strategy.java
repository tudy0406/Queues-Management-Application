package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.List;

public interface Strategy {
    void addTask(List<Server> servers, Task t);
}
