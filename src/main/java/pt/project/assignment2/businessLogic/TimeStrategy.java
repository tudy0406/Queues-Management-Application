package pt.project.assignment2.businessLogic;
import pt.project.assignment2.dataModel.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t){
        //To do Auto-generated method stub
        Server server = null;
        int minWaitingTime = Integer.MAX_VALUE;
        for(Server s : servers){
            if(s.getTasks().size() >= Scheduler.getMaxTasksPerServer())
                continue;
            if(s.getWaitingPeriod().intValue()<minWaitingTime){
                minWaitingTime = s.getWaitingPeriod().intValue();
                server = s;
            }
        }
        if(server != null){
            server.getTasks().add(t);
            server.setWaitingPeriod();
            System.out.println(t + " added successfully");
        }else{
            System.out.println("No server queue available");
        }
    }
}
