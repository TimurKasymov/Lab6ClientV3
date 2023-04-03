package interfaces;

import src.network.requests.Request;
import src.network.responses.Response;


public interface Command {
    /** executes the command */
    boolean execute(String[] args);
    /** prints the command description */
    void handleResponse(Response response);
}
