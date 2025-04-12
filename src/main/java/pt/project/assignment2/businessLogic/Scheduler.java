package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private Strategy strategy;

    public Scheduler(int maxNoServers, SelectionPolicy selectionPolicy) {
        this.maxNoServers = maxNoServers;
        changeStrategy(selectionPolicy);
        servers = new ArrayList<>();
        for(int i = 0; i < this.maxNoServers; i++) {
            Server server = new Server();
            Thread thread = new Thread(server);
            servers.add(server);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        switch (policy) {
            case SelectionPolicy.SHORTEST_QUEUE -> {
                strategy = new QueueStrategy();
                System.out.println("Shortest Queue strategy");
            }
            case SelectionPolicy.SHORTEST_TIME ->{
                strategy = new TimeStrategy();
                System.out.println("Shortest Time strategy");
            }
        }
    }

    public AtomicInteger dispatchTask(Task task) {
        return strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }
}
