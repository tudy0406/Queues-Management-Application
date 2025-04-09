package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface Strategy {
    AtomicInteger addTask(List<Server> servers, Task t);
}
