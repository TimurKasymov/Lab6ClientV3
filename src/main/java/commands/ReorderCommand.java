package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.ReorderRequest;
import src.network.responses.Response;

public class ReorderCommand extends CommandBase implements Command {

    public ReorderCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var request = new ReorderRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }
}
