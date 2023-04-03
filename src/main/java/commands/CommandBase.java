package commands;

import interfaces.CommandManagerCustom;
import src.network.requests.Request;
import src.network.responses.Response;

import java.io.IOException;

public class CommandBase {
    protected CommandManagerCustom commandManager;

    public CommandBase(CommandManagerCustom commandManager){
        this.commandManager = commandManager;
    }

    protected void sendToServer(Request request) {
        var data = commandManager.getSerializationManager().serialize(request);
        commandManager.getSendingManager().send(data);
    }

    protected void handleResponseByDefault(Response response){
        var commandMessageHandler = commandManager.getMessageHandler();
        if(response.getError()==null)
            commandMessageHandler.displayToUser(response.getMessageForClient());
        else
            commandMessageHandler.displayToUser(response.getError());
    }
}
