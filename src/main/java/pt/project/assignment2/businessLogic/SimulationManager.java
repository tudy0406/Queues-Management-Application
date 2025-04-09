package pt.project.assignment2.businessLogic;

import pt.project.assignment2.config.Constants;
import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;
import pt.project.assignment2.gui.SimulationFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        scheduler = new Scheduler(numberOfServers, maxTasksPerServer, selectionPolicy);

    }

    private void generateNRandomTasks(){
        int arrivalTime, serviceTime;
        Random random = new Random();
        for(int i = 0; i < numberOfClients; i++){
            arrivalTime = random.nextInt(timeLimit-maxProcessingTime);
            serviceTime = random.nextInt(minProcessingTime, maxProcessingTime);
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
        }
        generatedTasks.sort(Comparator.comparing(Task::getArrivalTime));
    }

    private float getAverageServiceTime(List<Task> tasks){
        int serviceTime = 0;
        for(Task task : tasks){
            serviceTime += task.getServiceTime();
        }
        return (float)serviceTime / tasks.size();
    }

    @Override
    public void run(){
        int currentTime = 0;
        float averageServiceTime = getAverageServiceTime(generatedTasks);
        File file = new File(Constants.FILE_NAME);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
        }catch(IOException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(writer);
        int i;
        int currentHourTasks;
        boolean empty = true;
        AtomicInteger waitingTime = new AtomicInteger(0);
        int numberOfTasks = generatedTasks.size();
        int maxNumberOfTasksPerHour = -1;
        int peakHour = -1;
        try {

            while (currentTime < timeLimit) {
                currentHourTasks = 0;
                //stuff
                Iterator<Task> iterator = generatedTasks.iterator();
                while(iterator.hasNext()){
                    Task task = iterator.next();
                    if(task.getArrivalTime() <= currentTime){
                        try{
                            waitingTime.addAndGet(scheduler.dispatchTask(task).get());
                            iterator.remove();
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                }

                printWriter.println("Time: " + currentTime);
                printWriter.print("Waiting queue: ");
                if(generatedTasks.isEmpty()){
                    printWriter.print("Empty");
                }else{
                    for (Task task : generatedTasks) {
                        if(task.getArrivalTime() <= currentTime)
                            currentHourTasks++;
                        printWriter.print(task + ", ");
                    }
                }
                printWriter.println();
                i = 1;
                for (Server server : scheduler.getServers()) {
                    printWriter.print("Queue " + i + ": ");
                    if(server.getTasks().isEmpty())
                        printWriter.print("empty");
                    else{
                        for (Task task : server.getTasks()) {
                            currentHourTasks++;
                            printWriter.print(task + ", ");
                        }
                    }
                    printWriter.println();
                    i++;
                }
                //wait an interval of 1 second
                try {
                    Thread.sleep(Constants.SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(currentHourTasks > maxNumberOfTasksPerHour){
                    maxNumberOfTasksPerHour = currentHourTasks;
                    peakHour = i;
                }
                currentTime++;
            }
            for (Server server : scheduler.getServers()){
                server.setRunning(false);
            }
            printWriter.println("Average waiting time: " + (float)waitingTime.get()/numberOfTasks);
            printWriter.println("Average service time: " + averageServiceTime);
            printWriter.println("Peak hour: " + peakHour);
            printWriter.close();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args){
        SimulationManager simulationManager = new SimulationManager(30, 10, 2, 2,4, 1);
        Thread t1 = new Thread(simulationManager);
        t1.start();
    }
}

