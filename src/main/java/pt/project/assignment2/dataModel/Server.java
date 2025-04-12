package pt.project.assignment2.dataModel;

import pt.project.assignment2.config.Constants;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingDeque<Task> tasks;
    private AtomicInteger waitingPeriod = new AtomicInteger(0);
    private boolean running = true;

    public Server() {
        this.tasks = new LinkedBlockingDeque<Task>();
        waitingPeriod.set(0);
    }

    public void run() {
        while(running) {
            try{
                Task task = tasks.peek();
                if(task != null){
                    Thread.sleep(Constants.SLEEP_TIME);
                    task.setServiceTime(task.getServiceTime() - 1);
                    this.waitingPeriod.decrementAndGet();
                    if(task.getServiceTime() == 0){
                        tasks.take();
                    }

                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public BlockingDeque<Task> getTasks(){
        return tasks;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setWaitingPeriod() {
        waitingPeriod.set(0);
        for (Task task : tasks) {
            waitingPeriod.addAndGet(task.getServiceTime());
        }
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}
