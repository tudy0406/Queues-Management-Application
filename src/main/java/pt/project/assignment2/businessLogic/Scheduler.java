package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private static List<Server> servers = new ArrayList<>();
    private static int maxNoServers;
    private static int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer, SelectionPolicy selectionPolicy) {
        Scheduler.maxNoServers = maxNoServers;
        Scheduler.maxTasksPerServer = maxTasksPerServer;
        changeStrategy(selectionPolicy);
        for(int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            Thread thread = new Thread(server);
            servers.add(server);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        switch (policy) {
            case SelectionPolicy.SHORTEST_QUEUE -> strategy = new QueueStrategy();
            case SelectionPolicy.SHORTEST_TIME -> strategy = new TimeStrategy();
        }
    }

    public AtomicInteger dispatchTask(Task task) {
        return strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public static int getMaxNoServers() {
        return maxNoServers;
    }

    public static int getMaxTasksPerServer() {
        return maxTasksPerServer;
    }
}
