package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.List;

public class Scheduler {
    private static List<Server> servers;
    private static int maxNoServers;
    private static int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        Scheduler.maxNoServers = maxNoServers;
        Scheduler.maxTasksPerServer = maxTasksPerServer;

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

    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }

    public static List<Server> getServers() {
        return servers;
    }

    public static int getMaxNoServers() {
        return maxNoServers;
    }

    public static int getMaxTasksPerServer() {
        return maxTasksPerServer;
    }
}
