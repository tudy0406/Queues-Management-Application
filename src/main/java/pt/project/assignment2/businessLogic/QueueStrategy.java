package pt.project.assignment2.businessLogic;

import pt.project.assignment2.dataModel.Server;
import pt.project.assignment2.dataModel.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueStrategy implements Strategy {

    @Override
    public AtomicInteger addTask(List<Server> servers, Task t){
        Server server = null;
        int minSize = Integer.MAX_VALUE;
        for(Server s : servers){
            if(s.getTasks().size() < minSize){
                minSize = s.getTasks().size();
                server = s;
            }
        }
        if(server!=null){
            server.getTasks().add(t);
            server.setWaitingPeriod();
            System.out.println(t + "added successfully");
            return server.getWaitingPeriod();
        }else{
            return new AtomicInteger(1);
        }
    }
}
