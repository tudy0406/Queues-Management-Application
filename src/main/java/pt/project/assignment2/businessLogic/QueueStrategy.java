package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.List;

public class QueueStrategy implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t){
        Server server = null;
        int minSize = Integer.MAX_VALUE;
        for(Server s : servers){
            if(s.getTasks().size()>=Scheduler.getMaxTasksPerServer())
                continue;
            if(s.getTasks().size() < minSize){
                minSize = s.getTasks().size();
                server = s;
            }
        }
        if(server!=null){
            server.getTasks().add(t);
            server.setWaitingPeriod();
            System.out.println(t + "added successfully");
        }else{
            System.out.println("No server queue available");
        }
    }
}
