package pt.project.assignment2.businessLogic;

import pt.project.assignment2.config.Constants;
import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;
import pt.project.assignment2.gui.SimulationFrameController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    //data read fromUI
    private int timeLimit = 100;
    private int minArrivalTime = 10;
    private int maxArrivalTime = 10;
    private int maxServiceTime = 10;
    private int minServiceTime = 2;
    private int numberOfServers = 3;
    private int numberOfClients = 100;
    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    //entity responsible with queue management and client distribution
    private Scheduler scheduler;
    //pool of tasks (client shopping in the store)
    private List<Task> generatedTasks;

    private static boolean running;

    public SimulationManager(int timeLimit, int numberOfServers, int numberOfClients, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy selectionPolicy) {
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.maxServiceTime = maxServiceTime;
        this.minServiceTime = minServiceTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.selectionPolicy = selectionPolicy;
        generatedTasks = new ArrayList<Task>();
        generateNRandomTasks();
        scheduler = new Scheduler(this.numberOfServers, this.selectionPolicy);
        running = true;

    }

    private void generateNRandomTasks(){
        int arrivalTime, serviceTime;
        Random random = new Random();
        for(int i = 0; i < numberOfClients; i++){
            arrivalTime = random.nextInt(minArrivalTime, maxArrivalTime);
            serviceTime = random.nextInt(minServiceTime, maxServiceTime);
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
        }
        generatedTasks.sort(Comparator.comparing(Task::getArrivalTime));
    }

    private float getAverageServiceTime(List<Task> tasks){
        int serviceTime = 0;
        for(Task task : tasks){
            serviceTime += task.getServiceTime();
        }
        return (float)serviceTime / numberOfClients;
    }

    @Override
    public void run(){
        int currentTime = 0;
        float averageServiceTime = getAverageServiceTime(generatedTasks);

        File file = new File(Constants.FILE_NAME);
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
        }catch(IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(writer);

        int i;
        int currentHourTasks;
        boolean empty;
        AtomicInteger waitingTime = new AtomicInteger(0);
        int numberOfTasks = generatedTasks.size();
        int maxNumberOfTasksPerHour = -1;
        int peakHour = -1;
        try {
            while (currentTime < timeLimit) {
                currentHourTasks = 0;
                empty = true;
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
                    empty = false;
                    for(Task task : generatedTasks){
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
                        empty = false;
                        for (Task task : server.getTasks()) {
                            currentHourTasks++;
                            printWriter.print(task + ", ");
                        }
                    }
                    printWriter.println();
                    i++;
                }
                printWriter.println();
                printWriter.flush();
                try {
                    Thread.sleep(Constants.SLEEP_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(currentHourTasks > maxNumberOfTasksPerHour){
                    maxNumberOfTasksPerHour = currentHourTasks;
                    peakHour = currentTime;
                }
                if(empty)
                    break;
                currentTime++;
            }

            for (Server server : scheduler.getServers()){
                server.setRunning(false);
            }
            printWriter.println("Average waiting time: " + (float)waitingTime.get()/numberOfTasks);
            printWriter.println("Average service time: " + averageServiceTime);
            printWriter.println("Peak hour: " + peakHour    );
            printWriter.close();
            Thread.sleep(Constants.SLEEP_TIME/2);
            stopSimulation();
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public static boolean getRunningState(){
        return running;
    }
    private void stopSimulation(){
        running = false;
        Thread.currentThread().interrupt();
    }
    public static void main(String[] args){
        SimulationManager simulationManager = new SimulationManager(200, 20,1000,10,100,3,9, SelectionPolicy.SHORTEST_TIME);
        Thread t1 = new Thread(simulationManager);
        t1.start();
    }

}

