package interfaces;


import src.network.Response;

public interface Command {
    /** executes the command */
    boolean execute(String[] args);
    /** prints the command description */
    void handleResponse(Response response);
}
