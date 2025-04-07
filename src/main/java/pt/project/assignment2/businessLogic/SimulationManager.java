package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Task;
import pt.project.assignment2.gui.SimulationFrame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable {
    //data read fromUI
    public int timeLimit = 100;
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 100;
    public int maxTasksPerServer = 2;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    //entity responsible with queue management and client distribution
    private Scheduler scheduler;
    //frame for displaying simulation
    private SimulationFrame frame;
    //pool of tasks (client shopping in the store)
    private List<Task> generatedTasks;

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int numberOfServers, int numberOfClients, int maxTasksPerServer) {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.maxTasksPerServer = maxTasksPerServer;
        generatedTasks = new ArrayList<Task>();
        generateNRandomTasks();
        scheduler = new Scheduler(maxProcessingTime, maxTasksPerServer);

    }

    private void generateNRandomTasks(){
        int arrivalTime, serviceTime;
        Random random = new Random();
        for(int i = 0; i < numberOfClients; i++){
            arrivalTime = random.nextInt(timeLimit);
            serviceTime = random.nextInt(minProcessingTime, maxProcessingTime);
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
        }
        generatedTasks.sort(Comparator.comparing(Task::getArrivalTime));
    }

    @Override
    public void run(){
        int currentTime = 0;
        while (currentTime < timeLimit){
            //stuff
            for(Task task : generatedTasks){
                if(task.getArrivalTime() <= currentTime){
                    scheduler.dispatchTask(task);
                }
            }
            currentTime++;
            //wait an interval of 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args){
        SimulationManager simulationManager = new SimulationManager(100, 10, 2, 3,100, 2);
        Thread t1 = new Thread(simulationManager);
        t1.start();
    }
}

