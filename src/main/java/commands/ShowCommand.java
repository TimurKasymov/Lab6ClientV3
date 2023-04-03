package commands;

import interfaces.Command;
import interfaces.CommandManagerCustom;
import src.network.requests.ShowRequest;
import src.network.responses.Response;


public class ShowCommand extends CommandBase implements Command {
    public ShowCommand(CommandManagerCustom commandManager){
        super(commandManager);
    }

    @Override
    public boolean execute(String[] args) {
        var commandMessageHandler = commandManager.getMessageHandler();
        var request = new ShowRequest();
        sendToServer(request);
        return true;
    }

    @Override
    public void handleResponse(Response response) {
        handleResponseByDefault(response);
    }

}
