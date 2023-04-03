package service;

import java.util.LinkedList;
import java.util.List;

public class MessageHandler {
    private final LinkedList<String> logs;
    {
        logs = new LinkedList<>();
    }
    /** displays the message to user */
    public void displayToUser(String message){
        System.out.println(message);
    }
    /** logs the message to log collection */
    public void log(String message){
        logs.add(message);
    }

    /**
     *  gets the logs collection
     * @return list of logs
     */
    public List<String> getLogs(){
        return logs;
    }
}
